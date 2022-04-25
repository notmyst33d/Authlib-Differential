package com.mojang.authlib.minecraft.client;

import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.yggdrasil.response.ErrorResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/minecraft/client/MinecraftClient.class */
public class MinecraftClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MinecraftClient.class);
    public static final int CONNECT_TIMEOUT_MS = 5000;
    public static final int READ_TIMEOUT_MS = 5000;
    private final String accessToken;
    private final Proxy proxy;
    private final ObjectMapper objectMapper = ObjectMapper.create();

    public MinecraftClient(String accessToken, Proxy proxy) {
        this.accessToken = (String) Validate.notNull(accessToken);
        this.proxy = (Proxy) Validate.notNull(proxy);
    }

    public <T> T get(URL url, Class<T> responseClass) {
        Validate.notNull(url);
        Validate.notNull(responseClass);
        HttpURLConnection connection = createUrlConnection(url);
        connection.setRequestProperty("Authorization", "Bearer " + this.accessToken);
        return (T) readInputStream(url, responseClass, connection);
    }

    public <T> T post(URL url, Object body, Class<T> responseClass) {
        Validate.notNull(url);
        Validate.notNull(body);
        Validate.notNull(responseClass);
        String bodyAsJson = this.objectMapper.writeValueAsString(body);
        byte[] postAsBytes = bodyAsJson.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection connection = postInternal(url, postAsBytes);
        return (T) readInputStream(url, responseClass, connection);
    }

    private <T> T readInputStream(URL url, Class<T> clazz, HttpURLConnection connection) {
        try {
            try {
                int status = connection.getResponseCode();
                if (status < 400) {
                    InputStream inputStream = connection.getInputStream();
                    String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    T t = (T) this.objectMapper.readValue(result, clazz);
                    IOUtils.closeQuietly(inputStream);
                    return t;
                }
                InputStream inputStream2 = connection.getErrorStream();
                if (inputStream2 != null) {
                    String result2 = IOUtils.toString(inputStream2, StandardCharsets.UTF_8);
                    ErrorResponse errorResponse = (ErrorResponse) this.objectMapper.readValue(result2, ErrorResponse.class);
                    throw new MinecraftClientHttpException(status, errorResponse);
                }
                throw new MinecraftClientHttpException(status);
            } catch (IOException e) {
                throw new MinecraftClientException(MinecraftClientException.ErrorType.SERVICE_UNAVAILABLE, "Failed to read from " + url + " due to " + e.getMessage(), e);
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly((InputStream) null);
            throw th;
        }
    }

    private HttpURLConnection postInternal(URL url, byte[] postAsBytes) {
        HttpURLConnection connection = createUrlConnection(url);
        OutputStream outputStream = null;
        try {
            try {
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
                connection.setRequestProperty("Authorization", "Bearer " + this.accessToken);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                outputStream = connection.getOutputStream();
                IOUtils.write(postAsBytes, outputStream);
                IOUtils.closeQuietly(outputStream);
                return connection;
            } catch (IOException io) {
                throw new MinecraftClientException(MinecraftClientException.ErrorType.SERVICE_UNAVAILABLE, "Failed to POST " + url, io);
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly(outputStream);
            throw th;
        }
    }

    private HttpURLConnection createUrlConnection(URL url) {
        try {
            LOGGER.debug("Connecting to {}", url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(this.proxy);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setUseCaches(false);
            return connection;
        } catch (IOException io) {
            throw new MinecraftClientException(MinecraftClientException.ErrorType.SERVICE_UNAVAILABLE, "Failed connecting to " + url, io);
        }
    }
}
