package com.discord.gateway.filter;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.discord.common.common.R;
import com.discord.common.exception.APIException;
import com.discord.gateway.pojo.LoginUser;
import com.discord.gateway.pojo.User;
import com.discord.gateway.utils.Constant;
import com.discord.gateway.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class JwtTokenAuthenticationFilter implements WebFilter {

    private final JwtUtils jwtUtils;
    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public JwtTokenAuthenticationFilter(JwtUtils jwtUtils, RedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        ServerHttpRequest serverHttpRequest = serverWebExchange.getRequest();
        String token = resolveToken(serverWebExchange.getRequest());
        if (StringUtils.hasLength(token)) {
            String tokenToString = jwtUtils.checkTokenToString(token);
            JSON json = JSONUtil.parse(redisTemplate.opsForValue().get(tokenToString));

            if (json == null) {
                ServerHttpResponse response = serverWebExchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                byte[] dataBytes;
                try {
                    dataBytes = objectMapper.writeValueAsBytes(R.error(Constant.UNAUTHORIZED_ERROR));
                } catch (JsonProcessingException e) {
                    throw new APIException(e.getMessage());
                }
                DataBuffer bodyDataBuffer = response.bufferFactory().wrap(dataBytes);
                return response.writeWith(Mono.just(bodyDataBuffer));
            }

            User user = JSONUtil.toBean((JSONObject) json, User.class);
            LoginUser loginUser = new LoginUser(user, user.getLabel());
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            String username = authentication.getName();
            ServerHttpRequest mutableReq = serverHttpRequest.mutate()
                    .header(Constant.HEADER_USER, username).build();
            ServerWebExchange mutableExchange = serverWebExchange.mutate().request(mutableReq).build();

            return webFilterChain.filter(mutableExchange).subscribeOn(Schedulers.boundedElastic())
                    .subscriberContext(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }
        return webFilterChain.filter(serverWebExchange);
    }

    /**
     * 从请求头中获取token
     *
     * @param request
     */
    private String resolveToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(Constant.TOKEN);
        if (StringUtils.hasLength(token)) {
            return token;
        }
        return null;
    }
}