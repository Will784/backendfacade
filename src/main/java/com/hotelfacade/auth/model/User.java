package com.hotelfacade.auth.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Registered user of the system. The password is never stored in plain text, only its BCrypt hash.
 */
public class User {

    private final String id;
    private final String fullName;
    private final String email;
    private final String passwordHash;
    private final Instant createdAt;

    public User(String fullName, String email, String passwordHash) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
