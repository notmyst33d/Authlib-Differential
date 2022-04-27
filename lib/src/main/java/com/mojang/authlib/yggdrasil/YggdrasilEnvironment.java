package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.function.Function;
import javax.annotation.Nullable;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/YggdrasilEnvironment.class */
public enum YggdrasilEnvironment {
    PROD("https://mcauth.ralsei.cf", "https://mcauth.ralsei.cf", "https://mcauth.ralsei.cf", "https://mcauth.ralsei.cf"),
    STAGING("https://yggdrasil-auth-staging.mojang.com", "https://api-staging.mojang.com", "https://api-sb-staging.minecraftservices.com", "https://api-staging.minecraftservices.com");
    
    private final Environment environment;

    YggdrasilEnvironment(String authHost, String accountsHost, String sessionHost, String servicesHost) {
        this.environment = Environment.create(authHost, accountsHost, sessionHost, servicesHost, name());
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public static Optional<Environment> fromString(@Nullable final String value) {
        return Stream.of(values()).filter(env -> value != null && value.equalsIgnoreCase(env.name())).findFirst().map((Function<? super YggdrasilEnvironment, ? extends Environment>)YggdrasilEnvironment::getEnvironment);
    }
}
