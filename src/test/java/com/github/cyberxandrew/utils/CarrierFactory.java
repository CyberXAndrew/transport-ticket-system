package com.github.cyberxandrew.utils;


import com.github.cyberxandrew.model.Carrier;

public class CarrierFactory {
    private static Long id = 1L;
    private static String name = "testCarrierName";
    private static String phoneNumber = "testPhoneNumber";

    public static Carrier createCarrierToSave() {
        Carrier carrier = new Carrier();
        carrier.setName(name);
        carrier.setPhoneNumber(phoneNumber);
        return carrier;
    }

    public static class CarrierBuilder {
        private Long id = 1L;
        private String name = "testCarrierName";
        private String phoneNumber = "testPhoneNumber";

        public CarrierBuilder withCarrierName(String name) {
            this.name = name;
            return this;
        }

        public CarrierBuilder withCarrierId (Long id) {
            this.id = id;
            return this;
        }

        public CarrierBuilder withCarrierPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Carrier build() {
            Carrier carrier = new Carrier();
            carrier.setId(id);
            carrier.setName(name);
            carrier.setPhoneNumber(phoneNumber);
            return carrier;
        }
    }
}
