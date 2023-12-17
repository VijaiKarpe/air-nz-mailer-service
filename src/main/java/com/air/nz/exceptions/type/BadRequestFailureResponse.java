package com.air.nz.exceptions.type;

public class BadRequestFailureResponse {
    public BadRequestFailureResponse()  {
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
