package com.mojang.authlib.yggdrasil;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.authlib.minecraft.TelemetryEvent;
import javax.annotation.Nullable;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/YggdrassilTelemetryEvent.class */
public class YggdrassilTelemetryEvent implements TelemetryEvent {
    private final YggdrassilTelemetrySession service;
    private final String type;
    @Nullable
    private JsonObject data = new JsonObject();

    /* JADX INFO: Access modifiers changed from: package-private */
    public YggdrassilTelemetryEvent(YggdrassilTelemetrySession service, String type) {
        this.service = service;
        this.type = type;
    }

    private JsonObject data() {
        if (this.data != null) {
            return this.data;
        }
        throw new IllegalStateException("Event already sent");
    }

    @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
    public void addProperty(String id, String value) {
        data().addProperty(id, value);
    }

    @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
    public void addProperty(String id, int value) {
        data().addProperty(id, Integer.valueOf(value));
    }

    @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
    public void addProperty(String id, boolean value) {
        data().addProperty(id, Boolean.valueOf(value));
    }

    @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
    public void addNullProperty(String id) {
        data().add(id, JsonNull.INSTANCE);
    }

    @Override // com.mojang.authlib.minecraft.TelemetryEvent
    public void send() {
        this.service.sendEvent(this.type, this.data);
        this.data = null;
    }
}
