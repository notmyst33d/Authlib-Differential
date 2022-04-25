package com.mojang.authlib.exceptions;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/exceptions/UserMigratedException.class */
public class UserMigratedException extends InvalidCredentialsException {
    public UserMigratedException() {
    }

    public UserMigratedException(String message) {
        super(message);
    }

    public UserMigratedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserMigratedException(Throwable cause) {
        super(cause);
    }
}
