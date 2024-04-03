package ru.kata.spring.boot_security.demo.util;

public class UserNotSavedException extends RuntimeException {
    public UserNotSavedException(String message) {
        super(message);
    }

}
