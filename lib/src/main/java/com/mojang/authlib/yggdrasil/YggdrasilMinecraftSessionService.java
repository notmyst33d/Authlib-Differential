package com.mojang.authlib.yggdrasil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.authlib.yggdrasil.response.HasJoinedMinecraftServerResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService.class */
public class YggdrasilMinecraftSessionService extends HttpMinecraftSessionService {
    private static final String BASE_URL = "https://mcauth.ralsei.cf/session/minecraft/";
    private final PublicKey publicKey;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private final LoadingCache<GameProfile, GameProfile> insecureProfiles = CacheBuilder.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).build(new CacheLoader<GameProfile, GameProfile>() { // from class: com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService.1
        public GameProfile load(GameProfile key) throws Exception {
            return YggdrasilMinecraftSessionService.this.fillGameProfile(key, false);
        }
    });
    private static final String[] WHITELISTED_DOMAINS = {"mcauth.ralsei.cf", ".minecraft.net"};
    private static final Logger LOGGER = LogManager.getLogger();
    private static final URL JOIN_URL = HttpAuthenticationService.constantURL("https://mcauth.ralsei.cf/session/minecraft/join");
    private static final URL CHECK_URL = HttpAuthenticationService.constantURL("https://mcauth.ralsei.cf/session/minecraft/hasJoined");

    /* JADX INFO: Access modifiers changed from: protected */
    public YggdrasilMinecraftSessionService(YggdrasilAuthenticationService authenticationService) {
        super(authenticationService);
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray(YggdrasilMinecraftSessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der")));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new Error("Missing/invalid yggdrasil public key!");
        }
    }

    @Override // com.mojang.authlib.minecraft.MinecraftSessionService
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        JoinMinecraftServerRequest request = new JoinMinecraftServerRequest();
        request.accessToken = authenticationToken;
        request.selectedProfile = profile.getId();
        request.serverId = serverId;
        getAuthenticationService().makeRequest(JOIN_URL, request, Response.class);
    }

    @Override // com.mojang.authlib.minecraft.MinecraftSessionService
    public GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("username", user.getName());
        arguments.put("serverId", serverId);
        if (address != null) {
            arguments.put("ip", address.getHostAddress());
        }
        URL url = HttpAuthenticationService.concatenateURL(CHECK_URL, HttpAuthenticationService.buildQuery(arguments));
        try {
            HasJoinedMinecraftServerResponse response = (HasJoinedMinecraftServerResponse) getAuthenticationService().makeRequest(url, null, HasJoinedMinecraftServerResponse.class);
            if (response == null || response.getId() == null) {
                return null;
            }
            GameProfile result = new GameProfile(response.getId(), user.getName());
            if (response.getProperties() != null) {
                result.getProperties().putAll(response.getProperties());
            }
            return result;
        } catch (AuthenticationUnavailableException e) {
            throw e;
        } catch (AuthenticationException e2) {
            return null;
        }
    }

    @Override // com.mojang.authlib.minecraft.MinecraftSessionService
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        Property textureProperty = (Property) Iterables.getFirst(profile.getProperties().get("textures"), (Object) null);
        if (textureProperty == null) {
            return new HashMap();
        }
        if (requireSecure) {
            if (!textureProperty.hasSignature()) {
                LOGGER.error("Signature is missing from textures payload");
                throw new InsecureTextureException("Signature is missing from textures payload");
            } else if (!textureProperty.isSignatureValid(this.publicKey)) {
                LOGGER.error("Textures payload has been tampered with (signature invalid)");
                throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
            }
        }
        try {
            String json = new String(Base64.decodeBase64(textureProperty.getValue()), Charsets.UTF_8);
            MinecraftTexturesPayload result = (MinecraftTexturesPayload) this.gson.fromJson(json, MinecraftTexturesPayload.class);
            if (result == null || result.getTextures() == null) {
                return new HashMap();
            }
            for (Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : result.getTextures().entrySet()) {
                if (!isWhitelistedDomain(entry.getValue().getUrl())) {
                    LOGGER.error("Textures payload has been tampered with (non-whitelisted domain)");
                    return new HashMap();
                }
            }
            return result.getTextures();
        } catch (JsonParseException e) {
            LOGGER.error("Could not decode textures payload", e);
            return new HashMap();
        }
    }

    @Override // com.mojang.authlib.minecraft.MinecraftSessionService
    public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
        if (profile.getId() == null) {
            return profile;
        }
        if (!requireSecure) {
            return (GameProfile) this.insecureProfiles.getUnchecked(profile);
        }
        return fillGameProfile(profile, true);
    }

    protected GameProfile fillGameProfile(GameProfile profile, boolean requireSecure) {
        try {
            URL url = HttpAuthenticationService.constantURL("https://mcauth.ralsei.cf/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(profile.getId()));
            MinecraftProfilePropertiesResponse response = (MinecraftProfilePropertiesResponse) getAuthenticationService().makeRequest(HttpAuthenticationService.concatenateURL(url, "unsigned=" + (!requireSecure)), null, MinecraftProfilePropertiesResponse.class);
            if (response == null) {
                LOGGER.debug("Couldn't fetch profile properties for " + profile + " as the profile does not exist");
                return profile;
            }
            GameProfile result = new GameProfile(response.getId(), response.getName());
            result.getProperties().putAll(response.getProperties());
            profile.getProperties().putAll(response.getProperties());
            LOGGER.debug("Successfully fetched profile properties for " + profile);
            return result;
        } catch (AuthenticationException e) {
            LOGGER.warn("Couldn't look up profile properties for " + profile, e);
            return profile;
        }
    }

    @Override // com.mojang.authlib.minecraft.HttpMinecraftSessionService, com.mojang.authlib.minecraft.BaseMinecraftSessionService
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService) super.getAuthenticationService();
    }

    private static boolean isWhitelistedDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            for (int i = 0; i < WHITELISTED_DOMAINS.length; i++) {
                if (domain.endsWith(WHITELISTED_DOMAINS[i])) {
                    return true;
                }
            }
            return false;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL '" + url + "'");
        }
    }
}
