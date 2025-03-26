package com.github.cyberxandrew.exception.carrier;

public class CarrierDeletionException extends  RuntimeException {
    public CarrierDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
    public CarrierDeletionException(String message) {
        super(message);
    }
}
