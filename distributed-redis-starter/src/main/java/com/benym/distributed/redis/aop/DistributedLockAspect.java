package com.benym.distributed.redis.aop;


import com.benym.distributed.redis.annotation.DistributedLock;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @date 2021/12/21 8:04 下午
 */
@Order(1)
@Aspect
@Scope
@Component
public class DistributedLockAspect {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockAspect.class);

    @Resource
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.benym.distributed.redis.annotation.DistributedLock)")
    public void lockPointCut() {

    }

    @Around("lockPointCut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) {
        // 从切面织入点处通过反射获得织入处方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = methodSignature.getMethod();
        // 获取注解处参数
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String lockKey = distributedLock.key();
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.unit();
        boolean fair = distributedLock.fair();
        // 分布式锁逻辑
        RLock lock;
        if (!fair) {
            lock = redissonClient.getLock(lockKey);
        } else {
            lock = redissonClient.getFairLock(lockKey);
        }
        Object result = null;
        try {
            boolean lockRes = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (lockRes) {
                result = joinPoint.proceed();
            } else {
                logger.error("获取分布式锁失败");
                throw new RuntimeException("获取分布式锁失败");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return result;
    }
}
