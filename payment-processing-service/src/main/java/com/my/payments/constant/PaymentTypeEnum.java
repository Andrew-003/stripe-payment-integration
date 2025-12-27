package com.my.payments.constant;

public enum PaymentTypeEnum {
    SALE(1, "SALE");

    private final int id;
    private final String name;

    PaymentTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static PaymentTypeEnum fromId(int id) {
        for (PaymentTypeEnum e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }

    public static PaymentTypeEnum fromName(String name) {
        for (PaymentTypeEnum e : values()) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        return null;
    }
}