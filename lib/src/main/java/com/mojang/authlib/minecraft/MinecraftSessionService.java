package com.mojang.authlib.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.net.InetAddress;
import java.util.Map;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/minecraft/MinecraftSessionService.class */
public interface MinecraftSessionService {
    void joinServer(GameProfile gameProfile, String str, String str2) throws AuthenticationException;

    GameProfile hasJoinedServer(GameProfile gameProfile, String str, InetAddress inetAddress) throws AuthenticationUnavailableException;

    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile, boolean z);

    GameProfile fillProfileProperties(GameProfile gameProfile, boolean z);
}
