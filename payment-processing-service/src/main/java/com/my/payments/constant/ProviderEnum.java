package com.my.payments.constant;

public enum ProviderEnum {
    STRIPE(1, "STRIPE");

    private final int id;
    private final String name;

    ProviderEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static ProviderEnum fromId(int id) {
        for (ProviderEnum e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }

    public static ProviderEnum fromName(String name) {
        for (ProviderEnum e : values()) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        return null;
    }
}