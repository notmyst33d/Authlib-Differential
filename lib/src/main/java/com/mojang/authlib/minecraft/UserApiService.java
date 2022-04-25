package com.mojang.authlib.minecraft;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/minecraft/UserApiService.class */
public interface UserApiService {
    public static final UserProperties OFFLINE_PROPERTIES = new UserProperties(Set.of(UserFlag.CHAT_ALLOWED, UserFlag.REALMS_ALLOWED, UserFlag.SERVERS_ALLOWED));
    public static final UserApiService OFFLINE = new UserApiService() { // from class: com.mojang.authlib.minecraft.UserApiService.1
        @Override // com.mojang.authlib.minecraft.UserApiService
        public UserProperties properties() {
            return OFFLINE_PROPERTIES;
        }

        @Override // com.mojang.authlib.minecraft.UserApiService
        public boolean isBlockedPlayer(UUID playerID) {
            return false;
        }

        @Override // com.mojang.authlib.minecraft.UserApiService
        public void refreshBlockList() {
        }

        @Override // com.mojang.authlib.minecraft.UserApiService
        public TelemetrySession newTelemetrySession(Executor executor) {
            return TelemetrySession.DISABLED;
        }
    };

    /* loaded from: authlib-3.3.39.jar:com/mojang/authlib/minecraft/UserApiService$UserFlag.class */
    public enum UserFlag {
        SERVERS_ALLOWED,
        REALMS_ALLOWED,
        CHAT_ALLOWED,
        TELEMETRY_ENABLED,
        PROFANITY_FILTER_ENABLED
    }

    UserProperties properties();

    boolean isBlockedPlayer(UUID uuid);

    void refreshBlockList();

    TelemetrySession newTelemetrySession(Executor executor);

    /* loaded from: authlib-3.3.39.jar:com/mojang/authlib/minecraft/UserApiService$UserProperties.class */
    public record UserProperties(Set<UserFlag> flags) {
        public boolean flag(final UserFlag flag) {
            return this.flags.contains(flag);
        }
    }
}
