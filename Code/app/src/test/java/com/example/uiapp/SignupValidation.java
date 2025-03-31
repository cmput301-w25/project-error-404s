package com.example.uiapp;


public class SignupValidation {


    /**
     * This validates the input fields
     * @param username the username input
     * @param password the password input
     * @return true if fields are non-empty and password is at least 6 characters
     */
    public boolean isValid(String username, String password) {
        return !(username.isEmpty() || password.isEmpty() || password.length() < 6);
    }
}
