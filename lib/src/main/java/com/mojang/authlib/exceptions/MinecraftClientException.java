package com.mojang.authlib.exceptions;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/exceptions/MinecraftClientException.class */
public class MinecraftClientException extends RuntimeException {
    protected final ErrorType type;

    /* loaded from: authlib-3.3.39.jar:com/mojang/authlib/exceptions/MinecraftClientException$ErrorType.class */
    public enum ErrorType {
        SERVICE_UNAVAILABLE,
        HTTP_ERROR,
        JSON_ERROR
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MinecraftClientException(ErrorType type, String message) {
        super(message);
        this.type = type;
    }

    public MinecraftClientException(ErrorType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public ErrorType getType() {
        return this.type;
    }

    public AuthenticationException toAuthenticationException() {
        return new AuthenticationException(this);
    }
}
