package com.mojang.authlib;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/GameProfileRepository.class */
public interface GameProfileRepository {
    void findProfilesByNames(String[] strArr, Agent agent, ProfileLookupCallback profileLookupCallback);
}
