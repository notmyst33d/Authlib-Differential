package com.mojang.authlib;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/GameProfileRepository.class */
public interface GameProfileRepository {
    void findProfilesByNames(String[] strArr, Agent agent, ProfileLookupCallback profileLookupCallback);
}
