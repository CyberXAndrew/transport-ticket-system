package com.github.cyberxandrew.exception.carrier;

public class CarrierNotFoundException extends RuntimeException {
    public CarrierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public CarrierNotFoundException(String message) {
        super(message);
    }
}
