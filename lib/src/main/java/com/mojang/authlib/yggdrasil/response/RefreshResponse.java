package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.GameProfile;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/yggdrasil/response/RefreshResponse.class */
public class RefreshResponse extends Response {
    private String accessToken;
    private String clientToken;
    private GameProfile selectedProfile;
    private GameProfile[] availableProfiles;
    private User user;

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getClientToken() {
        return this.clientToken;
    }

    public GameProfile[] getAvailableProfiles() {
        return this.availableProfiles;
    }

    public GameProfile getSelectedProfile() {
        return this.selectedProfile;
    }

    public User getUser() {
        return this.user;
    }
}
