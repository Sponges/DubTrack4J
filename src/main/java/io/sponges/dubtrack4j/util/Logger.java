package io.sponges.dubtrack4j.util;

import io.sponges.dubtrack4j.DubtrackAPIImpl;

public class Logger {

    public enum LoggingMode {
        WARNING, NORMAL, DEBUG
    }

    public static void debug(String msg) {
        debug(false, msg);
    }

    public static void debug(boolean bypass, String msg) {
        if (DubtrackAPIImpl.getLoggingMode() == LoggingMode.DEBUG || bypass) {
            System.out.println("DEBUG> " + msg);
        }
    }

    public static void info(String msg) {
        if (DubtrackAPIImpl.getLoggingMode() != LoggingMode.WARNING) {
            System.out.println("INFO> " + msg);
        }
    }

    public static void warning(String msg) {
        System.out.println("WARNING> " + msg);
    }

}
