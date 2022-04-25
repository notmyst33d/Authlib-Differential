package com.mojang.authlib.minecraft.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Objects;
import java.util.UUID;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/minecraft/client/ObjectMapper.class */
public class ObjectMapper {
    private final Gson gson;

    public ObjectMapper(Gson gson) {
        this.gson = (Gson) Objects.requireNonNull(gson);
    }

    public <T> T readValue(String value, Class<T> type) {
        try {
            return (T) this.gson.fromJson(value, type);
        } catch (JsonParseException e) {
            throw new MinecraftClientException(MinecraftClientException.ErrorType.JSON_ERROR, "Failed to read value " + value, e);
        }
    }

    public String writeValueAsString(Object entity) {
        try {
            return this.gson.toJson(entity);
        } catch (RuntimeException e) {
            throw new MinecraftClientException(MinecraftClientException.ErrorType.JSON_ERROR, "Failed to write value", e);
        }
    }

    public static ObjectMapper create() {
        return new ObjectMapper(new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create());
    }
}
