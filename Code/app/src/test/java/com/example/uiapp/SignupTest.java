package com.example.uiapp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * This class tests the validation logic used in signup
 */
public class SignupTest {

    private SignupValidation getValidator() {
        return new SignupValidation();
    }

    @Test
    public void testValidInput() {
        SignupValidation validator = getValidator();
        assertTrue(validator.isValid("john", "password123"));
    }

    @Test
    public void testEmptyUsername() {
        SignupValidation validator = getValidator();
        assertFalse(validator.isValid("", "password123"));
    }

    @Test
    public void testEmptyPassword() {
        SignupValidation validator = getValidator();
        assertFalse(validator.isValid("john", ""));
    }

    @Test
    public void testShortPassword() {
        SignupValidation validator = getValidator();
        assertFalse(validator.isValid("john", "123"));
    }
}
