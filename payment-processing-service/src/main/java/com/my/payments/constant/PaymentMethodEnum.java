package com.my.payments.constant;

public enum PaymentMethodEnum {
	APM(1, "APM");

    private final int id;
    private final String name;

    PaymentMethodEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static PaymentMethodEnum fromId(int id) {
        for (PaymentMethodEnum e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }

    public static PaymentMethodEnum fromName(String name) {
        for (PaymentMethodEnum e : values()) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        return null;
    }
}
