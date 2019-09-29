//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sdk.sLog.os;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadPool {
    private static ThreadPool _instance;

    protected ThreadPool() {
    }

    public static synchronized ThreadPool getInstance() {
        if (_instance == null) {
            _instance = new ThreadPool();
        }
        return _instance;
    }

    private static ExecutorService single = Executors.newSingleThreadExecutor();
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static ExecutorService ioExecutorService = new ThreadPoolExecutor(2 * CPU_COUNT + 1,
            2 * CPU_COUNT + 1,
            30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(128));

    public static void submitSingleTask(Runnable runnable) {
        single.execute(runnable);
    }

    public static void submitIoTask(Runnable runnable) {
        ioExecutorService.execute(runnable);
    }

    private static Executor UiExecutor = new MainThreadExecutor();

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    public static void submitUiThread(Runnable runnable) {
        UiExecutor.execute(runnable);
    }
}
