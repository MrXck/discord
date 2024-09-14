package com.discord.gateway.config;

import com.discord.gateway.filter.JwtTokenAuthenticationFilter;
import com.discord.gateway.handler.CustomHttpBasicServerAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
public class SecurityConfig {

    private final String[] excludeAuthPages = new String[]{"/user/login", "/user/register", "/user/sendMsg", "/user/loginBySms", "/login"};

    private final JwtTokenAuthenticationFilter filter;

    public SecurityConfig(JwtTokenAuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         CustomHttpBasicServerAuthenticationEntryPoint customHttpBasicServerAuthenticationEntryPoint) {
        http
                .requestCache()
                .requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .csrf().disable()
                .httpBasic()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customHttpBasicServerAuthenticationEntryPoint)// 自定义authenticationEntryPoint
                .accessDeniedHandler((swe, e) -> {
                    swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return swe.getResponse().writeWith(Mono.just(new DefaultDataBufferFactory().wrap("FORBIDDEN".getBytes())));
                })
                .and()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange()
                .pathMatchers(excludeAuthPages).permitAll()// 白名单
                .pathMatchers(HttpMethod.OPTIONS).permitAll()// option请求默认放行
                .pathMatchers("/**").hasRole("ADMIN")
                .anyExchange()
                .authenticated()
                .and()
                .addFilterAt(filter, SecurityWebFiltersOrder.HTTP_BASIC);
        return http.build();
    }
}