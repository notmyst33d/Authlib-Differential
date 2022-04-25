package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.GameProfile;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/request/RefreshRequest.class */
public class RefreshRequest {
    private String clientToken;
    private String accessToken;
    private GameProfile selectedProfile;
    private boolean requestUser;

    public RefreshRequest(String accessToken, String clientToken) {
        this(accessToken, clientToken, null);
    }

    public RefreshRequest(String accessToken, String clientToken, GameProfile profile) {
        this.requestUser = true;
        this.clientToken = clientToken;
        this.accessToken = accessToken;
        this.selectedProfile = profile;
    }
}
