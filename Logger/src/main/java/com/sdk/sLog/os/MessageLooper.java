package com.sdk.sLog.os;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MessageLooper
{
    public static MessageLooper thread(int count, final MessageProcessor processor)
    {
        ThreadMessageLooper threadMessageLooper = new ThreadMessageLooper(count);
        threadMessageLooper.addProcessor(processor);
        return threadMessageLooper;
    }

    public static MessageLooper adaptive(final MessageProcessor processor)
    {
        AdaptiveThreadMessageLooper adaptiveThreadMessageLooper = new AdaptiveThreadMessageLooper(30);
        adaptiveThreadMessageLooper.addProcessor(processor);
        return adaptiveThreadMessageLooper;
    }

    public static class Message implements Comparable<Message>
    {
        private int _type;
        private Object _arg;
        private long _when = 0;
        private Map<String, Object> _params;

        public int type()
        {
            return _type;
        }

        public Object arg()
        {
            return _arg;
        }

        public long when()
        {
            return _when;
        }

        public void when(long _when)
        {
            this._when = _when;
        }

        public Message(int type)
        {
            _type = type;
        }

        public Message(Runnable runnable)
        {
            arg(runnable);
        }

        public void arg(Object _arg0)
        {
            this._arg = _arg0;
        }

        public synchronized void put(String key, Object value)
        {
            if (_params == null)
            {
                _params = new HashMap<>();
            }

            _params.put(key, value);
        }

        public <T> T parameter(String key, T def)
        {
            if (_params == null)
                return def;

            if (_params.containsKey(key))
                return (T) _params.get(key);

            return def;
        }

        public Runnable runnable()
        {
            return (Runnable) _arg;
        }

        @Override
        public int compareTo(Message message)
        {
            if (when() > message.when())
                return 1;
            else if (when() == message.when())
                return 0;
            return -1;
        }
    }

    public interface WaitStrategy
    {
        void waitFor(long timeout) throws InterruptedException;

        void notifyHandle();
    }

    public interface MessageProcessor
    {
        void process(MessageLooper looper, Message message);
    }

    public interface QueueProvider
    {
        Queue<Message> create();
    }

    public static abstract class MessageLooperBase extends MessageLooper
    {
        private Queue<Message> _messageQueue;

        public MessageLooperBase()
        {
            if (getProvider() != null)
                _messageQueue = getProvider().create();
            else
                _messageQueue = new PriorityBlockingQueue<>();
        }

        public boolean hasMessage()
        {
            return !getMessageQueue().isEmpty();
        }

        private Queue<Message> getMessageQueue()
        {
            return _messageQueue;
        }

        public Object getMessage() throws InterruptedException
        {
            Message message = getMessageQueue().peek();

            if (message != null)
            {
                long time = message.when() - System.currentTimeMillis();
                if (message.when() == 0 || time <= 0)
                    return getMessageQueue().poll();
                else
                {
                    return time;
                }
            }

            return null;
        }

        private void waitForMessage(long timeout) throws InterruptedException
        {
            if (getWaitStrategy() != null)
                getWaitStrategy().waitFor(timeout);
        }

        boolean dispatchMessage() throws InterruptedException
        {
            Object msg = getMessage();

            if (msg == null)
                return false;

            if (msg instanceof Message)
            {
                onHandleMessage((Message) msg);
            }
            else
            {
                waitForMessage((Long) msg);
            }

            return true;
        }

        boolean handleMessage() throws InterruptedException
        {
            if (dispatchMessage())
            {
                return true;
            }
            else
            {
                waitForMessage(-1);
                return false;
            }
        }

        boolean handleMessage(long timeout) throws InterruptedException
        {
            if (dispatchMessage())
            {
                return true;
            }
            else
            {
                waitForMessage(timeout);
                return false;
            }
        }

        public void onHandleMessage(Message msg)
        {
            if (msg.runnable() != null)
                msg.runnable().run();
            else
                notifyMessage(this, msg);
        }

        @Override
        public void send(Message message)
        {
            getMessageQueue().offer(message);

            getWaitStrategy().notifyHandle();
        }

        Queue<Message> queue()
        {
            return _messageQueue;
        }
    }

    public static class ThreadMessageLooper extends MessageLooperBase
    {
        private int _count = 0;
        private ThreadGroup _group;

        public ThreadMessageLooper(int count)
        {
            _count = count;

            setWaitStrategy(new WaitStrategy()
            {
                @Override
                public void waitFor(long timeout) throws InterruptedException
                {
                    synchronized (this)
                    {
                        if (timeout != -1)
                            wait(timeout);
                        else
                            wait();
                    }
                }

                @Override
                public void notifyHandle()
                {
                    synchronized (this)
                    {
                        notify();
                    }
                }
            });
        }

        @Override
        public synchronized boolean loop()
        {
            if (_group != null)
            {
                return false;
            }

            _group = new ThreadGroup("MessageLoopGroup");
            for (int i = 0; i < _count; i++)
            {
                new Thread(_group, "MessageLoopThread [" + i + "]")
                {
                    @Override
                    public void run()
                    {
                        while (!isInterrupted())
                        {
                            try
                            {
                                handleMessage();
                            }
                            catch (InterruptedException e)
                            {
                                interrupt();
                            }
                        }
                    }
                }.start();
            }

            return true;
        }

        @Override
        public synchronized void interrupt()
        {
            if (_group != null)
            {
                _group.interrupt();
                _group = null;
            }
        }
    }

    public static class AdaptiveThreadMessageLooper extends MessageLooperBase
    {
        public static final int THREAD_COUNT = 3;
        public static final int THREAD_EXPIRES = 10 * 1000;
        private ThreadGroup _group;
        //空闲的线程数
        private AtomicInteger _free = new AtomicInteger(0);
        //总共线程数
        private AtomicInteger _count = new AtomicInteger(0);

        private int _max;

        public AdaptiveThreadMessageLooper(int max)
        {
            _max = max;
            _group = new ThreadGroup("MessageLoopGroup");
            setWaitStrategy(new WaitStrategy()
            {
                @Override
                public void waitFor(long timeout) throws InterruptedException
                {
                    synchronized (this)
                    {
                        if (timeout != -1)
                            wait(timeout);
                        else
                            wait();
                    }
                }

                @Override
                public void notifyHandle()
                {
                    synchronized (this)
                    {
                        notify();
                    }
                }
            });
        }

        @Override
        public synchronized boolean loop()
        {
            if (_group != null)
            {
                return false;
            }

            initThreads(3, 10 * 1000);

            return true;
        }

        private void initThreads(final int count, final long expires)
        {
            for (int i = 0; i < count; i++)
            {
                new Thread(_group, "MessageLoopThread [ " + _count.get() + " ]")
                {
                    private long _last = System.currentTimeMillis();

                    @Override
                    public void run()
                    {
                        _free.incrementAndGet();
                        while (!isInterrupted())
                        {
                            try
                            {
                                if (handleMessage())
                                    _last = System.currentTimeMillis();
                                else
                                {
                                    if (System.currentTimeMillis() - _last >= expires && _free.get() > count)
                                    {
                                        _free.decrementAndGet();
                                        _count.decrementAndGet();
                                        return;
                                    }
                                }
                            }
                            catch (InterruptedException e)
                            {
                                interrupt();
                            }
                        }
                    }
                }.start();
            }
        }

        @Override
        public void onHandleMessage(Message msg)
        {
            _free.decrementAndGet();
            super.onHandleMessage(msg);
            _free.incrementAndGet();
        }

        @Override
        public void send(Message message)
        {
            if (_free.get() == 0)
            {
                if (_count.get() < _max)
                {
                    _count.addAndGet(THREAD_COUNT);
                    initThreads(THREAD_COUNT, THREAD_EXPIRES);
                }
            }

            super.send(message);
        }

        @Override
        public synchronized void interrupt()
        {
            if (_group != null)
            {
                _group.interrupt();
                _group = null;
            }
        }

        int threadCount()
        {
            return _count.get();
        }

        int freeCount()
        {
            return _free.get();
        }
    }

    private WaitStrategy _waitStrategy;

    private QueueProvider _provider;

    private Collection<MessageProcessor> _messageListeners = new ArrayList<>();

    public void addProcessor(MessageProcessor processor)
    {
        _messageListeners.add(processor);
    }

    public void removeProcessor(MessageProcessor processor)
    {
        _messageListeners.remove(processor);
    }

    void notifyMessage(MessageLooper looper, Message msg)
    {
        for (MessageProcessor processor : _messageListeners)
        {
            processor.process(looper, msg);
        }
    }

    public WaitStrategy getWaitStrategy()
    {
        return _waitStrategy;
    }

    public void setWaitStrategy(WaitStrategy _waitStrategy)
    {
        this._waitStrategy = _waitStrategy;
    }

    public void setProvider(QueueProvider _provider)
    {
        this._provider = _provider;
    }

    public QueueProvider getProvider()
    {
        return _provider;
    }

    public abstract boolean loop();

    public abstract void interrupt();

    public abstract void send(Message message);

    public void send(int type)
    {
        send(new Message(type));
    }

    public void run(Runnable runnable)
    {
        send(new Message(runnable));
    }
}
