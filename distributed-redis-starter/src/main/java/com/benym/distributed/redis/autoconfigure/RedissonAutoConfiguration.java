package com.benym.distributed.redis.autoconfigure;

import javax.annotation.Resource;

import com.benym.distributed.redis.aop.DistributedLockAspect;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @date 2021/12/21 8:39 下午
 */
@Configuration
public class RedissonAutoConfiguration {

    @Resource
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient redissionSingleton() {
        Config config = new Config();
        String redisUrl = String.format("redis://%s:%s", redisProperties.getHost() + "",
                redisProperties.getPort() + "");
        config.useSingleServer()
                .setAddress(redisUrl)
                .setPassword(redisProperties.getPassword());
        return Redisson.create(config);
    }

    @Bean
    public DistributedLockAspect distributedLockAspect(){
        return new DistributedLockAspect();
    }

}
