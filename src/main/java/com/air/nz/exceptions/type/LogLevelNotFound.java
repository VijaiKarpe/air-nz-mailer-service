package com.air.nz.exceptions.type;

public class LogLevelNotFound extends RuntimeException {

    public String getLoglevel() {
        return loglevel;
    }

    private String loglevel;

    public LogLevelNotFound(String loglevel, String message) {
        super(message);
        this.loglevel = loglevel;
    }
}