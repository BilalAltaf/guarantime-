package com.guarantime;

import java.util.concurrent.ExecutionException;

public class ServiceException extends ExecutionException {
    public ServiceException(String message) {
        super(message);
    }
}
