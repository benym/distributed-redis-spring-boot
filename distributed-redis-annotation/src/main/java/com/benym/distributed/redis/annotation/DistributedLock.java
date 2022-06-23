package com.benym.distributed.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 分布式锁的key，一般是hash类型，这里是hash的名字
     */
    String key();

    /**
     * 上锁最长等待时间
     */
    long waitTime() default 60;

    /**
     * 上锁最长持有时间
     */
    long leaseTime() default -1;

    /**
     * 上锁时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 是否是公平锁
     */
    boolean fair() default false;
}
