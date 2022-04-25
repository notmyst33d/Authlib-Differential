package com.mojang.authlib.exceptions;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/exceptions/InsufficientPrivilegesException.class */
public class InsufficientPrivilegesException extends AuthenticationException {
    public InsufficientPrivilegesException() {
    }

    public InsufficientPrivilegesException(String message) {
        super(message);
    }

    public InsufficientPrivilegesException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientPrivilegesException(Throwable cause) {
        super(cause);
    }
}
