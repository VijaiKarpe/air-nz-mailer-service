package com.air.nz.exceptions.type;

public class LogLevelNotFoundResponse {

    private String errorMessage;
    private String logLevel;

    public LogLevelNotFoundResponse() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
}
