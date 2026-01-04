package com.rstracker.util;

import java.util.UUID;

public class RecordIdGenerator {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}

