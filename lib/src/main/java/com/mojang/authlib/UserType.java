package com.mojang.authlib;

import java.util.HashMap;
import java.util.Map;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/UserType.class */
public enum UserType {
    LEGACY("legacy"),
    MOJANG("mojang");
    
    private static final Map<String, UserType> BY_NAME = new HashMap();
    private final String name;

    static {
        UserType[] values;
        for (UserType type : values()) {
            BY_NAME.put(type.name, type);
        }
    }

    UserType(String name) {
        this.name = name;
    }

    public static UserType byName(String name) {
        return BY_NAME.get(name.toLowerCase());
    }

    public String getName() {
        return this.name;
    }
}
