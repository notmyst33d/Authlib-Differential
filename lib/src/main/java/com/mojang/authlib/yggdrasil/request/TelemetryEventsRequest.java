package com.mojang.authlib.yggdrasil.request;

import com.google.gson.JsonObject;
import java.time.Instant;
import java.util.List;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/request/TelemetryEventsRequest.class */
public class TelemetryEventsRequest {
    public final List<Event> events;

    public TelemetryEventsRequest(List<Event> events) {
        this.events = events;
    }

    /* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/request/TelemetryEventsRequest$Event.class */
    public static class Event {
        public final String source;
        public final String name;
        public final long timestamp;
        public final JsonObject data;

        public Event(String source, String name, Instant timestamp, JsonObject data) {
            this.source = source;
            this.name = name;
            this.timestamp = timestamp.getEpochSecond();
            this.data = data;
        }
    }
}
