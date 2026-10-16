package com.hotelfacade.auth;

import com.hotelfacade.auth.dto.AuthResponse;
import com.hotelfacade.auth.dto.LoginRequest;
import com.hotelfacade.auth.dto.RegisterRequest;
import com.hotelfacade.auth.model.User;
import com.hotelfacade.auth.repository.UserRepository;
import com.hotelfacade.common.exception.BusinessException;
import com.hotelfacade.security.JwtService;
import com.hotelfacade.security.LoginAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Registers users and validates their credentials, returning a JWT on success.
 */
@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS = "Correo o contraseña incorrectos.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginAttemptService = loginAttemptService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        String fullName = request.fullName().trim();
        User user = new User(fullName, email, passwordEncoder.encode(request.password()));

        if (!userRepository.saveIfAbsent(user)) {
            throw BusinessException.conflict("Ya existe una cuenta registrada con este correo.");
        }
        return buildResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        if (loginAttemptService.isBlocked(email)) {
            throw new BusinessException(HttpStatus.TOO_MANY_REQUESTS,
                    "Demasiados intentos fallidos. Espera " + loginAttemptService.getLockMinutes()
                            + " minutos e intenta de nuevo.");
        }

        User user = userRepository.findByEmail(email)
                .filter(found -> passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElse(null);

        if (user == null) {
            loginAttemptService.registerFailure(email);
            throw new BusinessException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS);
        }

        loginAttemptService.registerSuccess(email);
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        JwtService.IssuedToken token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token.value(), token.expiresAt(), user.getFullName(), user.getEmail());
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
