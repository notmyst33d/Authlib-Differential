package com.mojang.authlib;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/ProfileLookupCallback.class */
public interface ProfileLookupCallback {
    void onProfileLookupSucceeded(GameProfile gameProfile);

    void onProfileLookupFailed(GameProfile gameProfile, Exception exc);
}
