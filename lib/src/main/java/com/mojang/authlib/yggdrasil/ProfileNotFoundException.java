package com.mojang.authlib.yggdrasil;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/ProfileNotFoundException.class */
public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException() {
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileNotFoundException(Throwable cause) {
        super(cause);
    }
}
