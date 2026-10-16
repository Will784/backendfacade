package com.hotelfacade.auth.dto;

/**
 * Shared validation patterns. The frontend uses the same rules so the user sees them before sending.
 */
public final class ValidationRules {

    /** Letters (including Spanish accents) and single spaces. */
    public static final String PERSON_NAME = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü]+( [A-Za-zÁÉÍÓÚáéíóúÑñÜü]+)*$";

    /** At least one lower case letter, one upper case letter and one digit, without spaces. */
    public static final String STRONG_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)\\S+$";

    private ValidationRules() {
    }
}
