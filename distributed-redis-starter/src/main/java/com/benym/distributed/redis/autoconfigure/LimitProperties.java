package com.benym.distributed.redis.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @date 2021/12/23 4:50 下午
 */
@ConfigurationProperties(prefix = LimitProperties.PERFIX)
public class LimitProperties {

    public static final String PERFIX = "distributed.limit";

    /**
     * 限流次数
     */
    private long rate;

    /**
     * 限流间隔
     */
    private long rateInterval;

    /**
     * 是否限流所有接口
     */
    private boolean limitAll = false;

    /**
     * 自定义切点
     */
    private String customPointcut = "";

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getRateInterval() {
        return rateInterval;
    }

    public void setRateInterval(long rateInterval) {
        this.rateInterval = rateInterval;
    }

    public boolean isLimitAll() {
        return limitAll;
    }

    public void setLimitAll(boolean limitAll) {
        this.limitAll = limitAll;
    }

    public String getCustomPointcut() {
        return customPointcut;
    }

    public void setCustomPointcut(String customPointcut) {
        this.customPointcut = customPointcut;
    }
}
