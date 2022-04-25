package com.mojang.authlib.yggdrasil;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: authlib-3.3.39.jar:com/mojang/authlib/yggdrasil/YggdrasilGameProfileRepository.class */
public class YggdrasilGameProfileRepository implements GameProfileRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(YggdrasilGameProfileRepository.class);
    private final String searchPageUrl;
    private static final int ENTRIES_PER_PAGE = 2;
    private static final int MAX_FAIL_COUNT = 3;
    private static final int DELAY_BETWEEN_PAGES = 100;
    private static final int DELAY_BETWEEN_FAILURES = 750;
    private final YggdrasilAuthenticationService authenticationService;

    public YggdrasilGameProfileRepository(YggdrasilAuthenticationService authenticationService, Environment environment) {
        this.authenticationService = authenticationService;
        this.searchPageUrl = environment.getAccountsHost() + "/profiles/";
    }

    @Override // com.mojang.authlib.GameProfileRepository
    public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback callback) {
        boolean failed;
        GameProfile[] profiles;
        Set<String> criteria = Sets.newHashSet();
        for (String name : names) {
            if (!Strings.isNullOrEmpty(name)) {
                criteria.add(name.toLowerCase());
            }
        }
        for (List<String> request : Iterables.partition(criteria, (int) ENTRIES_PER_PAGE)) {
            int failCount = 0;
            do {
                failed = false;
                try {
                    ProfileSearchResultsResponse response = (ProfileSearchResultsResponse) this.authenticationService.makeRequest(HttpAuthenticationService.constantURL(this.searchPageUrl + agent.getName().toLowerCase()), request, ProfileSearchResultsResponse.class);
                    failCount = 0;
                    LOGGER.debug("Page {} returned {} results, parsing", 0, Integer.valueOf(response.getProfiles().length));
                    Set<String> missing = Sets.newHashSet(request);
                    for (GameProfile profile : response.getProfiles()) {
                        LOGGER.debug("Successfully looked up profile {}", profile);
                        missing.remove(profile.getName().toLowerCase());
                        callback.onProfileLookupSucceeded(profile);
                    }
                    for (String name2 : missing) {
                        LOGGER.debug("Couldn't find profile {}", name2);
                        callback.onProfileLookupFailed(new GameProfile(null, name2), new ProfileNotFoundException("Server did not find the requested profile"));
                    }
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                    }
                } catch (AuthenticationException e2) {
                    failCount++;
                    if (failCount == MAX_FAIL_COUNT) {
                        for (String name3 : request) {
                            LOGGER.debug("Couldn't find profile {} because of a server error", name3);
                            callback.onProfileLookupFailed(new GameProfile(null, name3), e2);
                        }
                    } else {
                        try {
                            Thread.sleep(750L);
                        } catch (InterruptedException e3) {
                        }
                        failed = true;
                    }
                }
            } while (failed);
        }
    }
}
