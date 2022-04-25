package com.mojang.authlib.yggdrasil;

import com.google.common.collect.ImmutableList;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.yggdrasil.request.TelemetryEventsRequest;
import com.mojang.authlib.yggdrasil.response.Response;
import java.util.List;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/YggdrassilTelemetrySession.class */
public class YggdrassilTelemetrySession implements TelemetrySession {
    private static final Logger LOGGER = LoggerFactory.getLogger(YggdrassilTelemetrySession.class);
    private static final String SOURCE = "minecraft.java";
    private final MinecraftClient minecraftClient;
    private final URL routeEvents;
    private final Executor ioExecutor;
    private final JsonObject globalProperties = new JsonObject();
    private Consumer<TelemetryPropertyContainer> eventSetupFunction = event -> {
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public YggdrassilTelemetrySession(MinecraftClient minecraftClient, Environment environment, Executor ioExecutor) {
        this.minecraftClient = minecraftClient;
        this.routeEvents = HttpAuthenticationService.constantURL(environment.getServicesHost() + "/events");
        this.ioExecutor = ioExecutor;
    }

    @Override // com.mojang.authlib.minecraft.TelemetrySession
    public boolean isEnabled() {
        return true;
    }

    @Override // com.mojang.authlib.minecraft.TelemetrySession
    public TelemetryEvent createNewEvent(String type) {
        return new YggdrassilTelemetryEvent(this, type);
    }

    @Override // com.mojang.authlib.minecraft.TelemetrySession
    public TelemetryPropertyContainer globalProperties() {
        return TelemetryPropertyContainer.forJsonObject(this.globalProperties);
    }

    @Override // com.mojang.authlib.minecraft.TelemetrySession
    public void eventSetupFunction(Consumer<TelemetryPropertyContainer> eventSetupFunction) {
        this.eventSetupFunction = eventSetupFunction;
    }

    public void sendEvent(final String type, final JsonObject data) {
        final Instant sendTime = Instant.now();
        this.globalProperties.entrySet().forEach(e -> data.add((String)e.getKey(), (JsonElement)e.getValue()));
        this.eventSetupFunction.accept(TelemetryPropertyContainer.forJsonObject(data));
        final TelemetryEventsRequest.Event request = new TelemetryEventsRequest.Event("minecraft.java", type, sendTime, data);
        this.ioExecutor.execute(() -> {
            try {
                final TelemetryEventsRequest envelope = new TelemetryEventsRequest((List<TelemetryEventsRequest.Event>)ImmutableList.of(request));
                this.minecraftClient.post(this.routeEvents, envelope, Response.class);
            }
            catch (final MinecraftClientException e2) {
                YggdrassilTelemetrySession.LOGGER.debug("Failed to send telemetry event {}", (Object)request.name, (Object)e2);
            }
        });
    }
}
