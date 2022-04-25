package com.mojang.authlib;

import com.mojang.authlib.properties.PropertyMap;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/GameProfile.class */
public class GameProfile {
    private final UUID id;
    private final String name;
    private final PropertyMap properties = new PropertyMap();
    private boolean legacy;

    public GameProfile(UUID id, String name) {
        if (id != null || !StringUtils.isBlank(name)) {
            this.id = id;
            this.name = name;
            return;
        }
        throw new IllegalArgumentException("Name and ID cannot both be blank");
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public PropertyMap getProperties() {
        return this.properties;
    }

    public boolean isComplete() {
        return this.id != null && StringUtils.isNotBlank(getName());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameProfile that = (GameProfile) o;
        if (this.id != null) {
            if (!this.id.equals(that.id)) {
                return false;
            }
        } else if (that.id != null) {
            return false;
        }
        if (this.name != null) {
            if (!this.name.equals(that.name)) {
                return false;
            }
            return true;
        } else if (that.name != null) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        return (31 * result) + (this.name != null ? this.name.hashCode() : 0);
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("name", this.name).append("properties", this.properties).append("legacy", this.legacy).toString();
    }

    public boolean isLegacy() {
        return this.legacy;
    }
}
