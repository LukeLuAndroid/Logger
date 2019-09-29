package com.sdk.sLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerFactory {

    private static Map<String, Logger> _logs = new ConcurrentHashMap<String, Logger>();

    public synchronized static Logger getLogger(String name) {
        return getLogger(name, null);
    }

    public synchronized static Logger getLogger(String name, LogConfiguration configuration) {
        if (_logs.containsKey(name))
            return _logs.get(name);

        Logger log = createLogger(name, configuration);

        _logs.put(name, log);

        return log;
    }

    public synchronized static Logger getLogger() {
        return getLogger("");
    }

    public synchronized static Logger getLogger(Class<?> cls) {
        return getLogger(cls.getSimpleName());
    }

    public synchronized static Logger getLogger(Class<?> cls, LogConfiguration configuration) {
        return getLogger(cls.getSimpleName(), configuration);
    }

    private static Logger createLogger(String name, LogConfiguration config) {
        Logger.Builder logger = new Logger.Builder()
                .setTag(name);
        if (config != null) {
            logger.setConfig(config);
        } else {
            logger.setConfig(sLog.instance().getConfig());
        }
        return logger.build();
    }
}
