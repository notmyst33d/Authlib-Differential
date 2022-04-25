package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.Agent;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/request/AuthenticationRequest.class */
public class AuthenticationRequest {
    private Agent agent;
    private String username;
    private String password;
    private String clientToken;
    private boolean requestUser = true;

    public AuthenticationRequest(Agent agent, String username, String password, String clientToken) {
        this.agent = agent;
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
    }
}
