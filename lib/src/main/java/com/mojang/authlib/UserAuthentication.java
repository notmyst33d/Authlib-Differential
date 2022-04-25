package com.mojang.authlib;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Map;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/UserAuthentication.class */
public interface UserAuthentication {
    boolean canLogIn();

    void logIn() throws AuthenticationException;

    void logOut();

    boolean isLoggedIn();

    boolean canPlayOnline();

    GameProfile[] getAvailableProfiles();

    GameProfile getSelectedProfile();

    void selectGameProfile(GameProfile gameProfile) throws AuthenticationException;

    void loadFromStorage(Map<String, Object> map);

    Map<String, Object> saveForStorage();

    void setUsername(String str);

    void setPassword(String str);

    String getAuthenticatedToken();

    String getUserID();

    PropertyMap getUserProperties();

    UserType getUserType();
}
