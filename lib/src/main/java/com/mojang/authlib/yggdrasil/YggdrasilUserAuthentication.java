package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/YggdrasilUserAuthentication.class */
public class YggdrasilUserAuthentication extends HttpUserAuthentication {
    private static final Logger LOGGER = LoggerFactory.getLogger(YggdrasilUserAuthentication.class);
    private final URL routeAuthenticate;
    private final URL routeRefresh;
    private final URL routeValidate;
    private final URL routeInvalidate;
    private final URL routeSignout;
    private static final String STORAGE_KEY_ACCESS_TOKEN = "accessToken";
    private final Agent agent;
    private GameProfile[] profiles;
    private final String clientToken;
    private String accessToken;
    private boolean isOnline;

    public YggdrasilUserAuthentication(YggdrasilAuthenticationService authenticationService, String clientToken, Agent agent) {
        this(authenticationService, clientToken, agent, YggdrasilEnvironment.PROD.getEnvironment());
    }

    public YggdrasilUserAuthentication(YggdrasilAuthenticationService authenticationService, String clientToken, Agent agent, Environment env) {
        super(authenticationService);
        this.clientToken = clientToken;
        this.agent = agent;
        LOGGER.info("Environment: " + env.getName(), ". AuthHost: " + env.getAuthHost());
        this.routeAuthenticate = HttpAuthenticationService.constantURL(env.getAuthHost() + "/authenticate");
        this.routeRefresh = HttpAuthenticationService.constantURL(env.getAuthHost() + "/refresh");
        this.routeValidate = HttpAuthenticationService.constantURL(env.getAuthHost() + "/validate");
        this.routeInvalidate = HttpAuthenticationService.constantURL(env.getAuthHost() + "/invalidate");
        this.routeSignout = HttpAuthenticationService.constantURL(env.getAuthHost() + "/signout");
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
            AuthenticationRequest request = new AuthenticationRequest(getAgent(), getUsername(), getPassword(), this.clientToken);
            AuthenticationResponse response = (AuthenticationResponse) getAuthenticationService().makeRequest(this.routeAuthenticate, request, AuthenticationResponse.class);
            if (!response.getClientToken().equals(this.clientToken)) {
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
        RefreshRequest request = new RefreshRequest(getAuthenticatedToken(), this.clientToken);
        RefreshResponse response = (RefreshResponse) getAuthenticationService().makeRequest(this.routeRefresh, request, RefreshResponse.class);
        if (!response.getClientToken().equals(this.clientToken)) {
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
        ValidateRequest request = new ValidateRequest(getAuthenticatedToken(), this.clientToken);
        try {
            getAuthenticationService().makeRequest(this.routeValidate, request, Response.class);
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
            RefreshRequest request = new RefreshRequest(getAuthenticatedToken(), this.clientToken, profile);
            RefreshResponse response = (RefreshResponse) getAuthenticationService().makeRequest(this.routeRefresh, request, RefreshResponse.class);
            if (!response.getClientToken().equals(this.clientToken)) {
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
        return "YggdrasilAuthenticationService{agent=" + this.agent + ", profiles=" + Arrays.toString(this.profiles) + ", selectedProfile=" + getSelectedProfile() + ", username='" + getUsername() + "', isLoggedIn=" + isLoggedIn() + ", userType=" + getUserType() + ", canPlayOnline=" + canPlayOnline() + ", accessToken='" + this.accessToken + "', clientToken='" + this.clientToken + "'}";
    }

    @Override // com.mojang.authlib.HttpUserAuthentication, com.mojang.authlib.BaseUserAuthentication
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService) super.getAuthenticationService();
    }
}
