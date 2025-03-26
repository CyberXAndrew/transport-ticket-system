package com.github.cyberxandrew.exception.carrier;

public class CarrierMappingException  extends RuntimeException {
    public CarrierMappingException(String message, Throwable cause) {
        super(message, cause);
    }
    public CarrierMappingException(String message) {
        super(message);
    }
}
