package com.github.cyberxandrew.exception.carrier;

public class CarrierSaveException extends RuntimeException {
    public CarrierSaveException(String message, Throwable cause) {
        super(message, cause);
    }
    public CarrierSaveException(String message) {
        super(message);
    }
}
