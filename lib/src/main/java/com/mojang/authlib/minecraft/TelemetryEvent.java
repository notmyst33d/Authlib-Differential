package com.mojang.authlib.minecraft;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/minecraft/TelemetryEvent.class */
public interface TelemetryEvent extends TelemetryPropertyContainer {
    public static final TelemetryEvent EMPTY = new TelemetryEvent() { // from class: com.mojang.authlib.minecraft.TelemetryEvent.1
        @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
        public void addProperty(String id, String value) {
        }

        @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
        public void addProperty(String id, int value) {
        }

        @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
        public void addProperty(String id, boolean value) {
        }

        @Override // com.mojang.authlib.minecraft.TelemetryPropertyContainer
        public void addNullProperty(String id) {
        }

        @Override // com.mojang.authlib.minecraft.TelemetryEvent
        public void send() {
        }
    };

    void send();
}
