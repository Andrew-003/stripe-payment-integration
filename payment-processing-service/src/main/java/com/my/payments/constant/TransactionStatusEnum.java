package com.my.payments.constant;

public enum TransactionStatusEnum {
    CREATED(1, "CREATED"),
    INITIATED(2, "INITIATED"),
    PENDING(3, "PENDING"),
    SUCCESS(4, "SUCCESS"),
    FAILED(5, "FAILED");

    private final int id;
    private final String name;

    TransactionStatusEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static TransactionStatusEnum fromId(int id) {
        for (TransactionStatusEnum e : values()) {
            if (e.id == id) return e;
        }
        return null;
    }

    public static TransactionStatusEnum fromName(String name) {
        for (TransactionStatusEnum e : values()) {
            if (e.name.equalsIgnoreCase(name)) return e;
        }
        return null;
    }
}
