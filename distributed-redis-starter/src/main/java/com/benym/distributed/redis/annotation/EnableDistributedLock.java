package com.benym.distributed.redis.annotation;

import com.benym.distributed.redis.autoconfigure.RedissonAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 此注解的作用是开启分布式锁开关，加此注解实际为了将bean注入到Spring
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RedissonAutoConfiguration.class})
public @interface EnableDistributedLock {

}
