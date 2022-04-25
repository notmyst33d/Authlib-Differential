package com.mojang.authlib.legacy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.HttpUserAuthentication;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.util.UUIDTypeAdapter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@Deprecated
/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/legacy/LegacyUserAuthentication.class */
public class LegacyUserAuthentication extends HttpUserAuthentication {
    private static final URL AUTHENTICATION_URL = HttpAuthenticationService.constantURL("https://login.minecraft.net");
    private static final int AUTHENTICATION_VERSION = 14;
    private static final int RESPONSE_PART_PROFILE_NAME = 2;
    private static final int RESPONSE_PART_SESSION_TOKEN = 3;
    private static final int RESPONSE_PART_PROFILE_ID = 4;
    private String sessionToken;

    /* JADX INFO: Access modifiers changed from: protected */
    public LegacyUserAuthentication(LegacyAuthenticationService authenticationService) {
        super(authenticationService);
    }

    @Override // com.mojang.authlib.UserAuthentication
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        } else if (StringUtils.isBlank(getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        } else {
            Map<String, Object> args = new HashMap<>();
            args.put("user", getUsername());
            args.put("password", getPassword());
            args.put("version", Integer.valueOf((int) AUTHENTICATION_VERSION));
            try {
                String response = getAuthenticationService().performPostRequest(AUTHENTICATION_URL, HttpAuthenticationService.buildQuery(args), "application/x-www-form-urlencoded").trim();
                String[] split = response.split(":");
                if (split.length == 5) {
                    String profileId = split[RESPONSE_PART_PROFILE_ID];
                    String profileName = split[RESPONSE_PART_PROFILE_NAME];
                    String sessionToken = split[RESPONSE_PART_SESSION_TOKEN];
                    if (StringUtils.isBlank(profileId) || StringUtils.isBlank(profileName) || StringUtils.isBlank(sessionToken)) {
                        throw new AuthenticationException("Unknown response from authentication server: " + response);
                    }
                    setSelectedProfile(new GameProfile(UUIDTypeAdapter.fromString(profileId), profileName));
                    this.sessionToken = sessionToken;
                    setUserType(UserType.LEGACY);
                    return;
                }
                throw new InvalidCredentialsException(response);
            } catch (IOException e) {
                throw new AuthenticationException("Authentication server is not responding", e);
            }
        }
    }

    @Override // com.mojang.authlib.BaseUserAuthentication, com.mojang.authlib.UserAuthentication
    public void logOut() {
        super.logOut();
        this.sessionToken = null;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public boolean canPlayOnline() {
        return (!isLoggedIn() || getSelectedProfile() == null || getAuthenticatedToken() == null) ? false : true;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public GameProfile[] getAvailableProfiles() {
        return getSelectedProfile() != null ? new GameProfile[]{getSelectedProfile()} : new GameProfile[0];
    }

    @Override // com.mojang.authlib.UserAuthentication
    public void selectGameProfile(GameProfile profile) throws AuthenticationException {
        throw new UnsupportedOperationException("Game profiles cannot be changed in the legacy authentication service");
    }

    @Override // com.mojang.authlib.UserAuthentication
    public String getAuthenticatedToken() {
        return this.sessionToken;
    }

    @Override // com.mojang.authlib.BaseUserAuthentication, com.mojang.authlib.UserAuthentication
    public String getUserID() {
        return getUsername();
    }

    @Override // com.mojang.authlib.HttpUserAuthentication, com.mojang.authlib.BaseUserAuthentication
    public LegacyAuthenticationService getAuthenticationService() {
        return (LegacyAuthenticationService) super.getAuthenticationService();
    }
}
