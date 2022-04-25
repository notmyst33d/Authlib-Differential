package com.mojang.authlib.legacy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import java.net.Proxy;
import org.apache.commons.lang3.Validate;

@Deprecated
/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/legacy/LegacyAuthenticationService.class */
public class LegacyAuthenticationService extends HttpAuthenticationService {
    protected LegacyAuthenticationService(Proxy proxy) {
        super(proxy);
    }

    @Override // com.mojang.authlib.AuthenticationService
    public LegacyUserAuthentication createUserAuthentication(Agent agent) {
        Validate.notNull(agent);
        if (agent == Agent.MINECRAFT) {
            return new LegacyUserAuthentication(this);
        }
        throw new IllegalArgumentException("Legacy authentication cannot handle anything but Minecraft");
    }

    @Override // com.mojang.authlib.AuthenticationService
    public LegacyMinecraftSessionService createMinecraftSessionService() {
        return new LegacyMinecraftSessionService(this);
    }

    @Override // com.mojang.authlib.AuthenticationService
    public GameProfileRepository createProfileRepository() {
        throw new UnsupportedOperationException("Legacy authentication service has no profile repository");
    }
}
