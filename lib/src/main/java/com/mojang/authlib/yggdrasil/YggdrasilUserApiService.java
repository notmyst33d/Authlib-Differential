package com.mojang.authlib.yggdrasil;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;
import java.net.Proxy;
import java.net.URL;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/YggdrasilUserApiService.class */
public class YggdrasilUserApiService implements UserApiService {
    private static final long BLOCKLIST_REQUEST_COOLDOWN_SECONDS = 120;
    private static final UUID ZERO_UUID = new UUID(0, 0);
    private final URL routePrivileges;
    private final URL routeBlocklist;
    private final MinecraftClient minecraftClient;
    private final Environment environment;
    private UserApiService.UserProperties properties = OFFLINE_PROPERTIES;
    @Nullable
    private Instant nextAcceptableBlockRequest;
    @Nullable
    private Set<UUID> blockList;

    public YggdrasilUserApiService(String accessToken, Proxy proxy, Environment env) throws AuthenticationException {
        this.minecraftClient = new MinecraftClient(accessToken, proxy);
        this.environment = env;
        this.routePrivileges = HttpAuthenticationService.constantURL(env.getServicesHost() + "/player/attributes");
        this.routeBlocklist = HttpAuthenticationService.constantURL(env.getServicesHost() + "/privacy/blocklist");
        fetchProperties();
    }

    @Override // com.mojang.authlib.minecraft.UserApiService
    public UserApiService.UserProperties properties() {
        return this.properties;
    }

    @Override // com.mojang.authlib.minecraft.UserApiService
    public TelemetrySession newTelemetrySession(Executor executor) {
        if (!this.properties.flag(UserApiService.UserFlag.TELEMETRY_ENABLED)) {
            return TelemetrySession.DISABLED;
        }
        return new YggdrassilTelemetrySession(this.minecraftClient, this.environment, executor);
    }

    @Override // com.mojang.authlib.minecraft.UserApiService
    public boolean isBlockedPlayer(UUID playerID) {
        if (playerID.equals(ZERO_UUID)) {
            return false;
        }
        if (this.blockList == null) {
            this.blockList = fetchBlockList();
            if (this.blockList == null) {
                return false;
            }
        }
        return this.blockList.contains(playerID);
    }

    @Override // com.mojang.authlib.minecraft.UserApiService
    public void refreshBlockList() {
        if (this.blockList == null || canMakeBlockListRequest()) {
            this.blockList = forceFetchBlockList();
        }
    }

    @Nullable
    private Set<UUID> fetchBlockList() {
        if (!canMakeBlockListRequest()) {
            return null;
        }
        return forceFetchBlockList();
    }

    private boolean canMakeBlockListRequest() {
        return this.nextAcceptableBlockRequest == null || Instant.now().isAfter(this.nextAcceptableBlockRequest);
    }

    private Set<UUID> forceFetchBlockList() {
        this.nextAcceptableBlockRequest = Instant.now().plusSeconds(BLOCKLIST_REQUEST_COOLDOWN_SECONDS);
        try {
            BlockListResponse response = (BlockListResponse) this.minecraftClient.get(this.routeBlocklist, BlockListResponse.class);
            return response.getBlockedProfiles();
        } catch (MinecraftClientHttpException e) {
            return null;
        } catch (MinecraftClientException e2) {
            return null;
        }
    }

    private void fetchProperties() throws AuthenticationException {
        try {
            UserAttributesResponse response = (UserAttributesResponse) this.minecraftClient.get(this.routePrivileges, UserAttributesResponse.class);
            ImmutableSet.Builder<UserApiService.UserFlag> flags = ImmutableSet.builder();
            UserAttributesResponse.Privileges privileges = response.getPrivileges();
            if (privileges != null) {
                addFlagIfUserHasPrivilege(privileges.getOnlineChat(), UserApiService.UserFlag.CHAT_ALLOWED, flags);
                addFlagIfUserHasPrivilege(privileges.getMultiplayerServer(), UserApiService.UserFlag.SERVERS_ALLOWED, flags);
                addFlagIfUserHasPrivilege(privileges.getMultiplayerRealms(), UserApiService.UserFlag.REALMS_ALLOWED, flags);
                addFlagIfUserHasPrivilege(privileges.getTelemetry(), UserApiService.UserFlag.TELEMETRY_ENABLED, flags);
            }
            UserAttributesResponse.ProfanityFilterPreferences profanityFilterPreferences = response.getProfanityFilterPreferences();
            if (profanityFilterPreferences != null && profanityFilterPreferences.isEnabled()) {
                flags.add(UserApiService.UserFlag.PROFANITY_FILTER_ENABLED);
            }
            this.properties = new UserApiService.UserProperties(flags.build());
        } catch (MinecraftClientHttpException e) {
            throw e.toAuthenticationException();
        } catch (MinecraftClientException e2) {
            throw e2.toAuthenticationException();
        }
    }

    private static void addFlagIfUserHasPrivilege(boolean privilege, UserApiService.UserFlag value, ImmutableSet.Builder<UserApiService.UserFlag> output) {
        if (privilege) {
            output.add(value);
        }
    }
}
