package com.benym.distributed.redis.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @date 2021/12/23 4:58 下午
 */
@Configuration
@EnableConfigurationProperties(value = LimitProperties.class)
@ConditionalOnProperty(prefix = LimitProperties.PERFIX, value = "enabled", havingValue = "true")
public class LimitAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(LimitAutoConfiguration.class);


}
