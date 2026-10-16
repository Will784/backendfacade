package com.hotelfacade.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporarily blocks an e-mail after too many failed logins, to slow down password guessing.
 */
@Service
public class LoginAttemptService {

    private final int maxAttempts;
    private final Duration lockDuration;
    private final Map<String, FailedAttempts> attemptsByEmail = new ConcurrentHashMap<>();

    public LoginAttemptService(@Value("${app.login.max-attempts}") int maxAttempts,
                               @Value("${app.login.lock-minutes}") long lockMinutes) {
        this.maxAttempts = maxAttempts;
        this.lockDuration = Duration.ofMinutes(lockMinutes);
    }

    public boolean isBlocked(String email) {
        FailedAttempts attempts = attemptsByEmail.get(email);
        if (attempts == null || attempts.lockedUntil() == null) {
            return false;
        }
        if (Instant.now().isAfter(attempts.lockedUntil())) {
            attemptsByEmail.remove(email);
            return false;
        }
        return true;
    }

    public void registerFailure(String email) {
        attemptsByEmail.compute(email, (key, current) -> {
            int count = current == null ? 1 : current.count() + 1;
            Instant lockedUntil = count >= maxAttempts ? Instant.now().plus(lockDuration) : null;
            return new FailedAttempts(count, lockedUntil);
        });
    }

    public void registerSuccess(String email) {
        attemptsByEmail.remove(email);
    }

    public long getLockMinutes() {
        return lockDuration.toMinutes();
    }

    private record FailedAttempts(int count, Instant lockedUntil) {
    }
}
