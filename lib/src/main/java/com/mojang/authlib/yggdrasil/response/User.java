package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;

/* loaded from: authlib-1.5.25.jar:com/mojang/authlib/yggdrasil/response/User.class */
public class User {
    private String id;
    private PropertyMap properties;

    public String getId() {
        return this.id;
    }

    public PropertyMap getProperties() {
        return this.properties;
    }
}
