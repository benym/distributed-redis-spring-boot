package com.benym.distributed.redis.autoconfigure;

import com.benym.distributed.redis.aop.advisor.DRateLimitAdvisor;
import com.benym.distributed.redis.aop.interceptor.DRateLimitInterceptor;
import com.benym.distributed.redis.aop.interceptor.DistributedRateLimitAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @date 2021/12/23 4:58 下午
 */
@Configuration
@EnableConfigurationProperties(value = LimitProperties.class)
@ConditionalOnProperty(prefix = LimitProperties.PERFIX, value = "enabled", havingValue = "true")
public class LimitAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(LimitAutoConfiguration.class);

    /**
     * 注入切面
     */
    @Bean
    public DistributedRateLimitAspect distributedRateLimitAspect() {
        return new DistributedRateLimitAspect();
    }

    /**
     * 注入Advisor
     */
    @Bean
    public DRateLimitAdvisor dRateLimitAdvisor(DistributedRateLimitAspect dratelimitAspect, LimitProperties limitProperties) {
        DRateLimitInterceptor dRateLimitInterceptor = new DRateLimitInterceptor(dratelimitAspect);
        return new DRateLimitAdvisor(dRateLimitInterceptor, limitProperties.getCustomPointcut());
    }
}
