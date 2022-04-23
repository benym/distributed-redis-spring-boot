package com.benym.distributed.redis.aop.interceptor;

import com.benym.distributed.redis.annotation.DistributedRateLimit;
import com.benym.distributed.redis.autoconfigure.LimitProperties;
import com.benym.distributed.redis.tools.IPUtils;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @date 2021/12/23 4:48 下午
 */
public class DistributedRateLimitAspect {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private LimitProperties limitProperties;

    private static final Logger logger = LoggerFactory.getLogger(DistributedRateLimitAspect.class);

    /**
     * 环绕切面，限流接口
     *
     * @param joinPoint joinPoint
     * @return Object
     */
    public Object around(ProceedingJoinPoint joinPoint) {

        // 从切面织入点处通过反射获得织入处方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = methodSignature.getMethod();
        // 获取注解处参数
        DistributedRateLimit disRateLimit = method.getAnnotation(DistributedRateLimit.class);
        // 判断是否全局限流
        if (limitAll()) {
            return null;
        }
        String defaultKey = disRateLimit.key();
        // 获取限流key，限流规则：默认key+IP+请求路径
        String key = getLimitKey(defaultKey);
        RateType rateType = disRateLimit.rateType();
        long rate = getRate(disRateLimit);
        long rateInterval = getRateInterval(disRateLimit);
        RateIntervalUnit unit = disRateLimit.unit();
        // 分布式限流逻辑
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        Object result = null;
        try {
            // trySetRate方法只会执行一次
            rateLimiter.trySetRate(rateType, rate, rateInterval, unit);
            // 如果redis内已有的速率和配置的速率不一样，则更新速率
            if (rateLimiter.getConfig().getRate() != rate) {
                rateLimiter.setRate(rateType, rate, rateInterval, unit);
            }
            boolean acquired = rateLimiter.tryAcquire(1);
            if (acquired) {
                result = joinPoint.proceed();
            } else {
                throw new RuntimeException("请求过于频繁");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

    /**
     * 异常切面
     *
     * @param e exception
     */
    public void afterThrowing(Exception e) {
        logger.error(e.getMessage());
    }

    /**
     * 根据注解和配置判断是否限流全部接口
     *
     * @return boolean
     */
    private boolean limitAll() {
        return limitProperties.isLimitAll();
    }

    /**
     * 获取限流key，限流规则：默认key:IP:请求路径
     *
     * @param defaultKey 默认key
     * @return String
     */
    private String getLimitKey(String defaultKey) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes == null) {
            return defaultKey;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String requestMethod = request.getRequestURI();
        String ip = IPUtils.getIpAddr(request);
        StringBuffer sbf = new StringBuffer();
        sbf.append(defaultKey);
        sbf.append(":");
        sbf.append(ip);
        sbf.append(requestMethod.replaceAll("/", ":"));
        return sbf.toString();
    }

    /**
     * 获取限流速率，@DistributedRateLimit > 全局配置
     *
     * @param limit limit
     * @return long
     */
    private long getRate(DistributedRateLimit limit) {
        if (limit != null) {
            return limit.rate();
        }
        return limitProperties.getRate();
    }

    /**
     * 获取限流间隔，@DistributedRateLimit > 全局配置
     *
     * @param limit limit
     * @return long
     */
    private long getRateInterval(DistributedRateLimit limit) {
        if (limit != null) {
            return limit.rateInterval();
        }
        return limitProperties.getRateInterval();
    }
}
