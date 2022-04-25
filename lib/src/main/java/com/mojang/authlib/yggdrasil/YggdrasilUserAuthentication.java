package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.HttpUserAuthentication;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.yggdrasil.request.AuthenticationRequest;
import com.mojang.authlib.yggdrasil.request.RefreshRequest;
import com.mojang.authlib.yggdrasil.request.ValidateRequest;
import com.mojang.authlib.yggdrasil.response.AuthenticationResponse;
import com.mojang.authlib.yggdrasil.response.RefreshResponse;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.authlib.yggdrasil.response.User;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/yggdrasil/YggdrasilUserAuthentication.class */
public class YggdrasilUserAuthentication extends HttpUserAuthentication {
    private static final String BASE_URL = "https://authserver.mojang.com/";
    private static final String STORAGE_KEY_ACCESS_TOKEN = "accessToken";
    private final Agent agent;
    private GameProfile[] profiles;
    private String accessToken;
    private boolean isOnline;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final URL ROUTE_AUTHENTICATE = HttpAuthenticationService.constantURL("https://authserver.mojang.com/authenticate");
    private static final URL ROUTE_REFRESH = HttpAuthenticationService.constantURL("https://authserver.mojang.com/refresh");
    private static final URL ROUTE_VALIDATE = HttpAuthenticationService.constantURL("https://authserver.mojang.com/validate");
    private static final URL ROUTE_INVALIDATE = HttpAuthenticationService.constantURL("https://authserver.mojang.com/invalidate");
    private static final URL ROUTE_SIGNOUT = HttpAuthenticationService.constantURL("https://authserver.mojang.com/signout");

    public YggdrasilUserAuthentication(YggdrasilAuthenticationService authenticationService, Agent agent) {
        super(authenticationService);
        this.agent = agent;
    }

    @Override // com.mojang.authlib.BaseUserAuthentication, com.mojang.authlib.UserAuthentication
    public boolean canLogIn() {
        return !canPlayOnline() && StringUtils.isNotBlank(getUsername()) && (StringUtils.isNotBlank(getPassword()) || StringUtils.isNotBlank(getAuthenticatedToken()));
    }

    @Override // com.mojang.authlib.UserAuthentication
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        } else if (StringUtils.isNotBlank(getAuthenticatedToken())) {
            logInWithToken();
        } else if (StringUtils.isNotBlank(getPassword())) {
            logInWithPassword();
        } else {
            throw new InvalidCredentialsException("Invalid password");
        }
    }

    protected void logInWithPassword() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        } else if (StringUtils.isBlank(getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        } else {
            LOGGER.info("Logging in with username & password");
            AuthenticationRequest request = new AuthenticationRequest(this, getUsername(), getPassword());
            AuthenticationResponse response = (AuthenticationResponse) getAuthenticationService().makeRequest(ROUTE_AUTHENTICATE, request, AuthenticationResponse.class);
            if (!response.getClientToken().equals(getAuthenticationService().getClientToken())) {
                throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
            }
            if (response.getSelectedProfile() != null) {
                setUserType(response.getSelectedProfile().isLegacy() ? UserType.LEGACY : UserType.MOJANG);
            } else if (ArrayUtils.isNotEmpty(response.getAvailableProfiles())) {
                setUserType(response.getAvailableProfiles()[0].isLegacy() ? UserType.LEGACY : UserType.MOJANG);
            }
            User user = response.getUser();
            if (user == null || user.getId() == null) {
                setUserid(getUsername());
            } else {
                setUserid(user.getId());
            }
            this.isOnline = true;
            this.accessToken = response.getAccessToken();
            this.profiles = response.getAvailableProfiles();
            setSelectedProfile(response.getSelectedProfile());
            getModifiableUserProperties().clear();
            updateUserProperties(user);
        }
    }

    protected void updateUserProperties(User user) {
        if (user != null && user.getProperties() != null) {
            getModifiableUserProperties().putAll(user.getProperties());
        }
    }

    protected void logInWithToken() throws AuthenticationException {
        if (StringUtils.isBlank(getUserID())) {
            if (StringUtils.isBlank(getUsername())) {
                setUserid(getUsername());
            } else {
                throw new InvalidCredentialsException("Invalid uuid & username");
            }
        }
        if (StringUtils.isBlank(getAuthenticatedToken())) {
            throw new InvalidCredentialsException("Invalid access token");
        }
        LOGGER.info("Logging in with access token");
        if (checkTokenValidity()) {
            LOGGER.debug("Skipping refresh call as we're safely logged in.");
            this.isOnline = true;
            return;
        }
        RefreshRequest request = new RefreshRequest(this);
        RefreshResponse response = (RefreshResponse) getAuthenticationService().makeRequest(ROUTE_REFRESH, request, RefreshResponse.class);
        if (!response.getClientToken().equals(getAuthenticationService().getClientToken())) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }
        if (response.getSelectedProfile() != null) {
            setUserType(response.getSelectedProfile().isLegacy() ? UserType.LEGACY : UserType.MOJANG);
        } else if (ArrayUtils.isNotEmpty(response.getAvailableProfiles())) {
            setUserType(response.getAvailableProfiles()[0].isLegacy() ? UserType.LEGACY : UserType.MOJANG);
        }
        if (response.getUser() == null || response.getUser().getId() == null) {
            setUserid(getUsername());
        } else {
            setUserid(response.getUser().getId());
        }
        this.isOnline = true;
        this.accessToken = response.getAccessToken();
        this.profiles = response.getAvailableProfiles();
        setSelectedProfile(response.getSelectedProfile());
        getModifiableUserProperties().clear();
        updateUserProperties(response.getUser());
    }

    protected boolean checkTokenValidity() throws AuthenticationException {
        ValidateRequest request = new ValidateRequest(this);
        try {
            getAuthenticationService().makeRequest(ROUTE_VALIDATE, request, Response.class);
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    @Override // com.mojang.authlib.BaseUserAuthentication, com.mojang.authlib.UserAuthentication
    public void logOut() {
        super.logOut();
        this.accessToken = null;
        this.profiles = null;
        this.isOnline = false;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public GameProfile[] getAvailableProfiles() {
        return this.profiles;
    }

    @Override // com.mojang.authlib.BaseUserAuthentication, com.mojang.authlib.UserAuthentication
    public boolean isLoggedIn() {
        return StringUtils.isNotBlank(this.accessToken);
    }

    @Override // com.mojang.authlib.UserAuthentication
    public boolean canPlayOnline() {
        return isLoggedIn() && getSelectedProfile() != null && this.isOnline;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public void selectGameProfile(GameProfile profile) throws AuthenticationException {
        if (!isLoggedIn()) {
            throw new AuthenticationException("Cannot change game profile whilst not logged in");
        } else if (getSelectedProfile() != null) {
            throw new AuthenticationException("Cannot change game profile. You must log out and back in.");
        } else if (profile == null || !ArrayUtils.contains(this.profiles, profile)) {
            throw new IllegalArgumentException("Invalid profile '" + profile + "'");
        } else {
            RefreshRequest request = new RefreshRequest(this, profile);
            RefreshResponse response = (RefreshResponse) getAuthenticationService().makeRequest(ROUTE_REFRESH, request, RefreshResponse.class);
            if (!response.getClientToken().equals(getAuthenticationService().getClientToken())) {
                throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
            }
            this.isOnline = true;
            this.accessToken = response.getAccessToken();
            setSelectedProfile(response.getSelectedProfile());
        }
    }

    @Override // com.mojang.authlib.BaseUserAuthentication, com.mojang.authlib.UserAuthentication
    public void loadFromStorage(Map<String, Object> credentials) {
        super.loadFromStorage(credentials);
        this.accessToken = String.valueOf(credentials.get(STORAGE_KEY_ACCESS_TOKEN));
    }

    @Override // com.mojang.authlib.BaseUserAuthentication, com.mojang.authlib.UserAuthentication
    public Map<String, Object> saveForStorage() {
        Map<String, Object> result = super.saveForStorage();
        if (StringUtils.isNotBlank(getAuthenticatedToken())) {
            result.put(STORAGE_KEY_ACCESS_TOKEN, getAuthenticatedToken());
        }
        return result;
    }

    @Deprecated
    public String getSessionToken() {
        if (!isLoggedIn() || getSelectedProfile() == null || !canPlayOnline()) {
            return null;
        }
        return String.format("token:%s:%s", getAuthenticatedToken(), getSelectedProfile().getId());
    }

    @Override // com.mojang.authlib.UserAuthentication
    public String getAuthenticatedToken() {
        return this.accessToken;
    }

    public Agent getAgent() {
        return this.agent;
    }

    @Override // com.mojang.authlib.BaseUserAuthentication
    public String toString() {
        return "YggdrasilAuthenticationService{agent=" + this.agent + ", profiles=" + Arrays.toString(this.profiles) + ", selectedProfile=" + getSelectedProfile() + ", username='" + getUsername() + "', isLoggedIn=" + isLoggedIn() + ", userType=" + getUserType() + ", canPlayOnline=" + canPlayOnline() + ", accessToken='" + this.accessToken + "', clientToken='" + getAuthenticationService().getClientToken() + "'}";
    }

    @Override // com.mojang.authlib.HttpUserAuthentication, com.mojang.authlib.BaseUserAuthentication
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService) super.getAuthenticationService();
    }
}
