package com.guangye.redis;

import com.guangye.redis.entity.ArticleEntity;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 序号${}
 *
 * @author menggy
 * @date 2020/4/13 13:26
 */
@Component
public class ArticleService {
    private final ZSetOperations<String,Object> zSetOperations;
    private final SetOperations<String,Object> setOperations;
    private final HashOperations<String,String,Object> hashOperations;
    private final static Integer  VOTE_SCORE=432;

    public ArticleService(ZSetOperations<String, Object> zSetOperations, SetOperations<String, Object> setOperations, HashOperations<String, String, Object> hashOperations) {
        this.zSetOperations = zSetOperations;
        this.setOperations = setOperations;
        this.hashOperations = hashOperations;
    }
    public void vote(String key){

    }
    public void publishArticle(ArticleEntity articleEntity){
        String voted="voted:"+articleEntity.getKey();

        setOperations.add(voted,articleEntity.getUser());
        setOperations.getOperations().expire(voted,7, TimeUnit.DAYS);

        Long timeStamp= Instant.now().toEpochMilli();
        String article="article:"+articleEntity.getKey();
        articleEntity.setVote(1);
        Map<String,Object> map=new HashMap<String,Object>(){{
            put("title",articleEntity.getTitle());
            put("link",articleEntity.getLink());
            put("voted",1);
            put("user",articleEntity.getUser());
            put("time",timeStamp);
        }};
        hashOperations.putAll(article,map);
        zSetOperations.add("score:",article,timeStamp+VOTE_SCORE);
        zSetOperations.add("time:",article,timeStamp);
    }
}
