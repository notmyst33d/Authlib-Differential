package com.mojang.authlib.exceptions;

import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.yggdrasil.response.ErrorResponse;
import java.util.Optional;
import java.util.StringJoiner;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/exceptions/MinecraftClientHttpException.class */
public class MinecraftClientHttpException extends MinecraftClientException {
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    private final int status;
    @Nullable
    private final ErrorResponse response;

    public MinecraftClientHttpException(int status) {
        super(MinecraftClientException.ErrorType.HTTP_ERROR, getErrorMessage(status, null));
        this.status = status;
        this.response = null;
    }

    public MinecraftClientHttpException(int status, ErrorResponse response) {
        super(MinecraftClientException.ErrorType.HTTP_ERROR, getErrorMessage(status, response));
        this.status = status;
        this.response = response;
    }

    public int getStatus() {
        return this.status;
    }

    public Optional<ErrorResponse> getResponse() {
        return Optional.ofNullable(this.response);
    }

    @Override // java.lang.Throwable
    public String toString() {
        return new StringJoiner(", ", MinecraftClientHttpException.class.getSimpleName() + "[", "]").add("type=" + this.type).add("status=" + this.status).add("response=" + this.response).toString();
    }

    @Override // com.mojang.authlib.exceptions.MinecraftClientException
    public AuthenticationException toAuthenticationException() {
        if (hasError("InsufficientPrivilegesException") || this.status == 403) {
            return new InsufficientPrivilegesException(getMessage(), this);
        }
        if (this.status == 401) {
            return new InvalidCredentialsException(getMessage(), this);
        }
        if (this.status >= 500) {
            return new AuthenticationUnavailableException(getMessage(), this);
        }
        return new AuthenticationException(getMessage(), this);
    }

    private Optional<String> getError() {
        return getResponse().map((v0) -> {
            return v0.getError();
        }).filter((v0) -> {
            return StringUtils.isNotEmpty(v0);
        });
    }

    private static String getErrorMessage(int status, ErrorResponse response) {
        String errorMessage;
        if (response == null) {
            errorMessage = "Status: " + status;
        } else if (StringUtils.isNotEmpty(response.getErrorMessage())) {
            errorMessage = response.getErrorMessage();
        } else if (StringUtils.isNotEmpty(response.getError())) {
            errorMessage = response.getError();
        } else {
            errorMessage = "Status: " + status;
        }
        return errorMessage;
    }

    private boolean hasError(String error) {
        return getError().filter(value -> {
            return value.equalsIgnoreCase(error);
        }).isPresent();
    }
}
