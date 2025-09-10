package com.example.redisdatastructure.hash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "user_session", timeToLive = 1800)
public class UserSession {

    @Id
    private String sessionId;

    @Indexed
    private String userId;

    private String username;
    private String lastAccessPage;

    public void changeLastAccessPage(String page){
        lastAccessPage=page;
    }
}
