package com.hotelfacade.auth.dto;

import java.time.Instant;

/**
 * Data returned after a successful login or registration.
 */
public record AuthResponse(String token, Instant expiresAt, String fullName, String email) {
}
