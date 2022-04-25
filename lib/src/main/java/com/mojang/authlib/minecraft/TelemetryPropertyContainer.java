package com.mojang.authlib.minecraft;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/minecraft/TelemetryPropertyContainer.class */
public interface TelemetryPropertyContainer {
    void addProperty(String str, String str2);

    void addProperty(String str, int i);

    void addProperty(String str, boolean z);

    void addNullProperty(String str);

    static TelemetryPropertyContainer forJsonObject(final JsonObject object) {
        return new TelemetryPropertyContainer() { // from class: com.mojang.authlib.minecraft.TelemetryPropertyContainer.1
            @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
            public void addProperty(String id, String value) {
                object.addProperty(id, value);
            }

            @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
            public void addProperty(String id, int value) {
                object.addProperty(id, Integer.valueOf(value));
            }

            @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
            public void addProperty(String id, boolean value) {
                object.addProperty(id, Boolean.valueOf(value));
            }

            @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
            public void addNullProperty(String id) {
                object.add(id, JsonNull.INSTANCE);
            }
        };
    }
}
