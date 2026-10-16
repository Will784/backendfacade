package com.hotelfacade.auth.repository;

import com.hotelfacade.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory user storage. The key is the e-mail in lower case.
 */
@Repository
public class UserRepository {

    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email);
    }

    /**
     * Saves the user only if the e-mail is not taken yet.
     *
     * @return true when the user was saved
     */
    public boolean saveIfAbsent(User user) {
        return usersByEmail.putIfAbsent(user.getEmail(), user) == null;
    }
}
