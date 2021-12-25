package com.benym.distributed.redis.aop.advisor;

import com.benym.distributed.redis.aop.interceptor.DRateLimitInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * @date 2021/12/24 8:20 下午
 */
public class DRateLimitAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private static String DEFAULT_POINTCUT = "@within(org.springframework.stereotype.Controller)"
            + "|| @within(org.springframework.web.bind.annotation.RestController)";
    private final Advice advice;
    private final Pointcut pointcut;
    private final String customPointcut;

    public DRateLimitAdvisor(@NonNull DRateLimitInterceptor dRateLimitInterceptor, String customPointcut) {
        advice = dRateLimitInterceptor;
        this.customPointcut = customPointcut;
        pointcut = buildPointcut();
    }

    private Pointcut buildPointcut() {
        AspectJExpressionPointcut ajpc = new AspectJExpressionPointcut();
        ajpc.setExpression(buildCutExpression(customPointcut));
        return ajpc;
    }

    private String buildCutExpression(String customPointcut) {
        StringBuffer sbf = new StringBuffer(DEFAULT_POINTCUT);
        if (!StringUtils.isEmpty(customPointcut)) {
            sbf.append(" || ").append(customPointcut);
        }
        return sbf.toString();
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) advice).setBeanFactory(beanFactory);
        }
    }
}
