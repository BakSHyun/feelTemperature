package com.rstracker.entity;

public enum MatchingStatus {
    WAITING("waiting"),
    ESTABLISHED("established"),
    COMPLETED("completed");

    private final String value;

    MatchingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MatchingStatus fromValue(String value) {
        for (MatchingStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}

