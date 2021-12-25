package com.benym.distributed.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedRateLimit {

    /**
     * 限流key
     */
    String key() default "";

    /**
     * 限流类型
     */
    RateType rateType() default RateType.PER_CLIENT;

    /**
     * 限流数
     */
    long rate() default 5;

    /**
     * 限流时间间隔
     */
    long rateInterval() default 1;

    /**
     * 限流时间单位
     */
    RateIntervalUnit unit() default RateIntervalUnit.SECONDS;
}
