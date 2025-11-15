package com.cygnet.domain.constants;

import java.time.Duration;

public class RedisConstants {

    private static final String RedisKeyPrefix = "Web project:";

    private static final String LoginTokenKeyPrefix = RedisKeyPrefix + "LoginToken:";

    public static final Duration USER_TOKEN_TIMEOUT = Duration.ofDays(7);;

    public static String LoginTokenKey(Long id) {
        return LoginTokenKeyPrefix + id;
    }

}
