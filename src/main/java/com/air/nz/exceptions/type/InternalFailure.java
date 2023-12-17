package com.air.nz.exceptions.type;

import org.springframework.beans.factory.annotation.Value;

public class InternalFailure extends RuntimeException {

    public InternalFailure(String message) {
        super(message);
    }
}


