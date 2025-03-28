package com.github.cyberxandrew.exception.carrier;

public class CarrierHasRoutesException extends RuntimeException {
    public CarrierHasRoutesException(String message, Throwable cause) {
        super(message, cause);
    }
    public CarrierHasRoutesException(String message) {
        super(message);
    }
}
