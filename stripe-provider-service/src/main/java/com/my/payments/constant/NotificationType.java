package com.my.payments.constant;

public enum NotificationType {
    PAYMENT_SUCCESS(1, "PAYMENT_SUCCESS"),
    PAYMENT_FAILED(2, "PAYMENT_FAILED");

    private final int id;
    private final String name;

    NotificationType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static NotificationType fromId(int id) {
        for (NotificationType e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }

    public static NotificationType fromName(String name) {
        for (NotificationType e : values()) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        return null;
    }
}
