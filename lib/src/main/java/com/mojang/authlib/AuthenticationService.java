package com.mojang.authlib;

import com.mojang.authlib.minecraft.MinecraftSessionService;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/AuthenticationService.class */
public interface AuthenticationService {
    UserAuthentication createUserAuthentication(Agent agent);

    MinecraftSessionService createMinecraftSessionService();

    GameProfileRepository createProfileRepository();
}
