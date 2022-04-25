package com.mojang.authlib;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/BaseUserAuthentication.class */
public abstract class BaseUserAuthentication implements UserAuthentication {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUserAuthentication.class);
    protected static final String STORAGE_KEY_PROFILE_NAME = "displayName";
    protected static final String STORAGE_KEY_PROFILE_ID = "uuid";
    protected static final String STORAGE_KEY_PROFILE_PROPERTIES = "profileProperties";
    protected static final String STORAGE_KEY_USER_NAME = "username";
    protected static final String STORAGE_KEY_USER_ID = "userid";
    protected static final String STORAGE_KEY_USER_PROPERTIES = "userProperties";
    private final AuthenticationService authenticationService;
    private final PropertyMap userProperties = new PropertyMap();
    private String userid;
    private String username;
    private String password;
    private GameProfile selectedProfile;
    private UserType userType;

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseUserAuthentication(AuthenticationService authenticationService) {
        Validate.notNull(authenticationService);
        this.authenticationService = authenticationService;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public boolean canLogIn() {
        return !canPlayOnline() && StringUtils.isNotBlank(getUsername()) && StringUtils.isNotBlank(getPassword());
    }

    @Override // com.mojang.authlib.UserAuthentication
    public void logOut() {
        this.password = null;
        this.userid = null;
        setSelectedProfile(null);
        getModifiableUserProperties().clear();
        setUserType(null);
    }

    @Override // com.mojang.authlib.UserAuthentication
    public boolean isLoggedIn() {
        return getSelectedProfile() != null;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public void setUsername(String username) {
        if (!isLoggedIn() || !canPlayOnline()) {
            this.username = username;
            return;
        }
        throw new IllegalStateException("Cannot change username whilst logged in & online");
    }

    @Override // com.mojang.authlib.UserAuthentication
    public void setPassword(String password) {
        if (!isLoggedIn() || !canPlayOnline() || !StringUtils.isNotBlank(password)) {
            this.password = password;
            return;
        }
        throw new IllegalStateException("Cannot set password whilst logged in & online");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getUsername() {
        return this.username;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getPassword() {
        return this.password;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public void loadFromStorage(Map<String, Object> credentials) {
        logOut();
        setUsername(String.valueOf(credentials.get(STORAGE_KEY_USER_NAME)));
        if (credentials.containsKey(STORAGE_KEY_USER_ID)) {
            this.userid = String.valueOf(credentials.get(STORAGE_KEY_USER_ID));
        } else {
            this.userid = this.username;
        }
        if (credentials.containsKey(STORAGE_KEY_USER_PROPERTIES)) {
            try {
                List<Map<String, String>> list = (List) credentials.get(STORAGE_KEY_USER_PROPERTIES);
                for (Map<String, String> propertyMap : list) {
                    String name = propertyMap.get("name");
                    String value = propertyMap.get("value");
                    String signature = propertyMap.get("signature");
                    if (signature == null) {
                        getModifiableUserProperties().put(name, new Property(name, value));
                    } else {
                        getModifiableUserProperties().put(name, new Property(name, value, signature));
                    }
                }
            } catch (Throwable t) {
                LOGGER.warn("Couldn't deserialize user properties", t);
            }
        }
        if (credentials.containsKey(STORAGE_KEY_PROFILE_NAME) && credentials.containsKey(STORAGE_KEY_PROFILE_ID)) {
            GameProfile profile = new GameProfile(UUIDTypeAdapter.fromString(String.valueOf(credentials.get(STORAGE_KEY_PROFILE_ID))), String.valueOf(credentials.get(STORAGE_KEY_PROFILE_NAME)));
            if (credentials.containsKey(STORAGE_KEY_PROFILE_PROPERTIES)) {
                try {
                    List<Map<String, String>> list2 = (List) credentials.get(STORAGE_KEY_PROFILE_PROPERTIES);
                    for (Map<String, String> propertyMap2 : list2) {
                        String name2 = propertyMap2.get("name");
                        String value2 = propertyMap2.get("value");
                        String signature2 = propertyMap2.get("signature");
                        if (signature2 == null) {
                            profile.getProperties().put(name2, new Property(name2, value2));
                        } else {
                            profile.getProperties().put(name2, new Property(name2, value2, signature2));
                        }
                    }
                } catch (Throwable t2) {
                    LOGGER.warn("Couldn't deserialize profile properties", t2);
                }
            }
            setSelectedProfile(profile);
        }
    }

    @Override // com.mojang.authlib.UserAuthentication
    public Map<String, Object> saveForStorage() {
        Map<String, Object> result = new HashMap<>();
        if (getUsername() != null) {
            result.put(STORAGE_KEY_USER_NAME, getUsername());
        }
        if (getUserID() != null) {
            result.put(STORAGE_KEY_USER_ID, getUserID());
        } else if (getUsername() != null) {
            result.put(STORAGE_KEY_USER_NAME, getUsername());
        }
        if (!getUserProperties().isEmpty()) {
            List<Map<String, String>> properties = new ArrayList<>();
            for (Property userProperty : getUserProperties().values()) {
                Map<String, String> property = new HashMap<>();
                property.put("name", userProperty.getName());
                property.put("value", userProperty.getValue());
                property.put("signature", userProperty.getSignature());
                properties.add(property);
            }
            result.put(STORAGE_KEY_USER_PROPERTIES, properties);
        }
        GameProfile selectedProfile = getSelectedProfile();
        if (selectedProfile != null) {
            result.put(STORAGE_KEY_PROFILE_NAME, selectedProfile.getName());
            result.put(STORAGE_KEY_PROFILE_ID, selectedProfile.getId());
            List<Map<String, String>> properties2 = new ArrayList<>();
            for (Property profileProperty : selectedProfile.getProperties().values()) {
                Map<String, String> property2 = new HashMap<>();
                property2.put("name", profileProperty.getName());
                property2.put("value", profileProperty.getValue());
                property2.put("signature", profileProperty.getSignature());
                properties2.add(property2);
            }
            if (!properties2.isEmpty()) {
                result.put(STORAGE_KEY_PROFILE_PROPERTIES, properties2);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setSelectedProfile(GameProfile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public GameProfile getSelectedProfile() {
        return this.selectedProfile;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getClass().getSimpleName());
        result.append("{");
        if (isLoggedIn()) {
            result.append("Logged in as ");
            result.append(getUsername());
            if (getSelectedProfile() != null) {
                result.append(" / ");
                result.append(getSelectedProfile());
                result.append(" - ");
                if (canPlayOnline()) {
                    result.append("Online");
                } else {
                    result.append("Offline");
                }
            }
        } else {
            result.append("Not logged in");
        }
        result.append("}");
        return result.toString();
    }

    public AuthenticationService getAuthenticationService() {
        return this.authenticationService;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public String getUserID() {
        return this.userid;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public PropertyMap getUserProperties() {
        if (!isLoggedIn()) {
            return new PropertyMap();
        }
        PropertyMap result = new PropertyMap();
        result.putAll(getModifiableUserProperties());
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PropertyMap getModifiableUserProperties() {
        return this.userProperties;
    }

    @Override // com.mojang.authlib.UserAuthentication
    public UserType getUserType() {
        if (isLoggedIn()) {
            return this.userType == null ? UserType.LEGACY : this.userType;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setUserid(String userid) {
        this.userid = userid;
    }
}
