package elevator;

public class Logger {

    private static java.util.logging.Logger logger;

    private Logger(){}

    private static java.util.logging.Logger getLogger() {
        if (logger == null) {
            logger = java.util.logging.Logger.getAnonymousLogger();
        }
        return logger;
    }

    public static void warning(String msg){
        getLogger().warning(msg);
    }

    public static void info(String msg){
        getLogger().info(msg);
    }

    public static void severe(String msg){
        getLogger().severe(msg);
    }

    public static void fine(String msg){
        getLogger().fine(msg);
    }

    public static void finer(String msg){
        getLogger().finer(msg);
    }

    public static void finest(String msg){
        getLogger().finest(msg);
    }
}
