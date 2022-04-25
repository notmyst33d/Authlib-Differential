package com.mojang.authlib.exceptions;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/exceptions/InvalidCredentialsException.class */
public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException() {
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialsException(Throwable cause) {
        super(cause);
    }
}
