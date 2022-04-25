package com.mojang.authlib.yggdrasil.response;

import java.util.Set;
import java.util.UUID;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/response/BlockListResponse.class */
public class BlockListResponse extends Response {
    private Set<UUID> blockedProfiles;

    public Set<UUID> getBlockedProfiles() {
        return this.blockedProfiles;
    }
}
