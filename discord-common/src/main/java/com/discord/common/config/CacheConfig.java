package com.discord.common.config;

import com.discord.common.utils.CacheUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheUtils<String, Object> cacheUtils() {
        return new CacheUtils<>();
    }
}
