package com.github.cyberxandrew.exception.carrier;

public class CarrierUpdateException extends RuntimeException {
    public CarrierUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
    public CarrierUpdateException(String message) {
        super(message);
    }
}
