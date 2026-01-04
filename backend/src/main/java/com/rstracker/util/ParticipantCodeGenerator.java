package com.rstracker.util;

import java.util.UUID;

public class ParticipantCodeGenerator {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}

