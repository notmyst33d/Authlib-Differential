package com.mojang.authlib.yggdrasil.response;

import javax.annotation.Nullable;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/response/UserAttributesResponse.class */
public class UserAttributesResponse extends Response {
    @Nullable
    private Privileges privileges;
    @Nullable
    private ProfanityFilterPreferences profanityFilterPreferences;

    @Nullable
    public Privileges getPrivileges() {
        return this.privileges;
    }

    @Nullable
    public ProfanityFilterPreferences getProfanityFilterPreferences() {
        return this.profanityFilterPreferences;
    }

    /* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/response/UserAttributesResponse$Privileges.class */
    public static class Privileges {
        @Nullable
        private Privilege onlineChat;
        @Nullable
        private Privilege multiplayerServer;
        @Nullable
        private Privilege multiplayerRealms;
        @Nullable
        private Privilege telemetry;

        public boolean getOnlineChat() {
            return this.onlineChat != null && this.onlineChat.enabled;
        }

        public boolean getMultiplayerServer() {
            return this.multiplayerServer != null && this.multiplayerServer.enabled;
        }

        public boolean getMultiplayerRealms() {
            return this.multiplayerRealms != null && this.multiplayerRealms.enabled;
        }

        public boolean getTelemetry() {
            return this.telemetry != null && this.telemetry.enabled;
        }

        /* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/response/UserAttributesResponse$Privileges$Privilege.class */
        public class Privilege {
            private boolean enabled;

            public Privilege() {
            }
        }
    }

    /* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/response/UserAttributesResponse$ProfanityFilterPreferences.class */
    public static class ProfanityFilterPreferences {
        private boolean profanityFilterOn;

        public boolean isEnabled() {
            return this.profanityFilterOn;
        }
    }
}
