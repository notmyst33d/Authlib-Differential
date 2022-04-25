package com.mojang.authlib;

import java.util.StringJoiner;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/Environment.class */
public interface Environment {
    String getAuthHost();

    String getAccountsHost();

    String getSessionHost();

    String getServicesHost();

    String getName();

    String asString();

    static Environment create(final String auth, final String account, final String session, final String services, final String name) {
        return new Environment() { // from class: com.mojang.authlib.Environment.1
            @Override // com.mojang.authlib.Environment
            public String getAuthHost() {
                return auth;
            }

            @Override // com.mojang.authlib.Environment
            public String getAccountsHost() {
                return account;
            }

            @Override // com.mojang.authlib.Environment
            public String getSessionHost() {
                return session;
            }

            @Override // com.mojang.authlib.Environment
            public String getServicesHost() {
                return services;
            }

            @Override // com.mojang.authlib.Environment
            public String getName() {
                return name;
            }

            @Override // com.mojang.authlib.Environment
            public String asString() {
                return new StringJoiner(", ", "", "").add("authHost='" + getAuthHost() + "'").add("accountsHost='" + getAccountsHost() + "'").add("sessionHost='" + getSessionHost() + "'").add("servicesHost='" + getServicesHost() + "'").add("name='" + getName() + "'").toString();
            }
        };
    }
}
