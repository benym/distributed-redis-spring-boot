package com.benym.distributed.redis.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;

/**
 * @date 2021/12/24 4:42 下午
 */
public class DRateLimitInterceptor implements MethodInterceptor {

    private DistributedRateLimitAspect drateLimitAspect;

    public DRateLimitInterceptor(DistributedRateLimitAspect drateLimitAspect) {
        this.drateLimitAspect = drateLimitAspect;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!(invocation instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException(
                    "MethodInvocation is not a Spring ProxyMethodInvocation");
        }
        ProxyMethodInvocation pmi = (ProxyMethodInvocation) invocation;
        ProceedingJoinPoint pjp = lazyInitJoinPoint(pmi);
        return doAround(pjp);
    }

    private ProceedingJoinPoint lazyInitJoinPoint(ProxyMethodInvocation pmi) {
        return new MethodInvocationProceedingJoinPoint(pmi);
    }

    private Object doAround(ProceedingJoinPoint pjp) {
        try {
            return drateLimitAspect.around(pjp);
        } catch (Exception e) {
            afterThrowing(e);
            throw e;
        }
    }

    private void afterThrowing(Exception e) {
        drateLimitAspect.afterThrowing(e);
    }
}
