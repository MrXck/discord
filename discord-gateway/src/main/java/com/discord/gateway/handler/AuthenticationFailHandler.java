package com.discord.gateway.handler;

import com.discord.common.common.R;
import com.discord.gateway.utils.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFailHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException e) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders headers = response.getHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Cache-Control", "no-store,no-cache,must-revalidate,max-age-8");
        headers.add("Access-Control-Allow-Origin", exchange.getRequest().getHeaders().getOrigin());
        headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");//允许请求的方法;
        byte[] dataBytes = {};
        ObjectMapper mapper = new ObjectMapper();
        try {
            dataBytes = mapper.writeValueAsBytes(R.error(Constant.UNAUTHORIZED_ERROR));
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }

        DataBuffer bodyDateBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDateBuffer));
    }
}