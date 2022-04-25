package com.mojang.authlib.minecraft;

import java.util.function.Consumer;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/minecraft/TelemetrySession.class */
public interface TelemetrySession {
    public static final TelemetrySession DISABLED = new TelemetrySession() { // from class: com.mojang.authlib.minecraft.TelemetrySession.1
        @Override // com.mojang.authlib.minecraft.TelemetrySession
        public boolean isEnabled() {
            return false;
        }

        @Override // com.mojang.authlib.minecraft.TelemetrySession
        public TelemetryPropertyContainer globalProperties() {
            return TelemetryEvent.EMPTY;
        }

        @Override // com.mojang.authlib.minecraft.TelemetrySession
        public void eventSetupFunction(Consumer<TelemetryPropertyContainer> event) {
        }

        @Override // com.mojang.authlib.minecraft.TelemetrySession
        public TelemetryEvent createNewEvent(String type) {
            return TelemetryEvent.EMPTY;
        }
    };

    boolean isEnabled();

    TelemetryPropertyContainer globalProperties();

    void eventSetupFunction(Consumer<TelemetryPropertyContainer> consumer);

    TelemetryEvent createNewEvent(String str);
}
