package com.mojang.authlib;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/ProfileLookupCallback.class */
public interface ProfileLookupCallback {
    void onProfileLookupSucceeded(GameProfile gameProfile);

    void onProfileLookupFailed(GameProfile gameProfile, Exception exc);
}
