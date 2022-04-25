package com.mojang.authlib.minecraft;

import com.mojang.authlib.AuthenticationService;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/minecraft/BaseMinecraftSessionService.class */
public abstract class BaseMinecraftSessionService implements MinecraftSessionService {
    private final AuthenticationService authenticationService;

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseMinecraftSessionService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public AuthenticationService getAuthenticationService() {
        return this.authenticationService;
    }
}
