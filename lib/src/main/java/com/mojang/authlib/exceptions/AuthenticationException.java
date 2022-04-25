package com.mojang.authlib.exceptions;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/exceptions/AuthenticationException.class */
public class AuthenticationException extends Exception {
    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
