package io.stream.com.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.stream.com.models.EmailTokenTTL;
import io.stream.com.utils.TimeUtil;

@Service
public class EmailCache {
    
    private static final String EMAIL_VERIFICATION_TOKEN_KEY = "viewing:history:movie";

    // 15 minutes in milliseconds
    private static final int EXPIRATION_TIME = 900000;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String getEmail(String token){

        return getEmailOf(redisTemplate.opsForHash().get(EMAIL_VERIFICATION_TOKEN_KEY, token));
    }

    public void addToken(String token, String email){ 

        redisTemplate.opsForHash().put(EMAIL_VERIFICATION_TOKEN_KEY, token, new EmailTokenTTL(email, TimeUtil.currentTimeInMillisecondsAfter(EXPIRATION_TIME)));
    }

    public boolean isValidToken(String token){

        if (!redisTemplate.opsForHash().hasKey(EMAIL_VERIFICATION_TOKEN_KEY, token))
            return false;

        return isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){

        return System.currentTimeMillis() < getGeneratedTime(redisTemplate.opsForHash().get(EMAIL_VERIFICATION_TOKEN_KEY, token));
    }

    private Long getGeneratedTime(Object object){

        return ((EmailTokenTTL) object).getGeneratedTime();
    }

    private String getEmailOf(Object object){

        return ((EmailTokenTTL) object).getEmail();
    }

}
