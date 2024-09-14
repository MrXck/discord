package com.discord.gateway.handler;

import com.discord.common.common.R;
import com.discord.gateway.VO.UserVO;
import com.discord.gateway.pojo.LoginUser;
import com.discord.gateway.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationSuccessHandler extends WebFilterChainServerAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;

    public AuthenticationSuccessHandler(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        //设置headers
        HttpHeaders headers = response.getHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Cache-Control", "no-store,no-cache,must-revalidate,max-age-8");
        headers.add("Access-Control-Allow-Origin", exchange.getRequest().getHeaders().getOrigin());
        headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");//允许请求的方法;
        byte[] dataBytes;
        ObjectMapper mapper = new ObjectMapper();
        try {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            UserVO userVO = new UserVO();
            userVO.setUser(loginUser.getUser());
            userVO.setToken(jwtUtils.getToken(loginUser.getUser().getId()));
            dataBytes = mapper.writeValueAsBytes(R.success(userVO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            dataBytes = "".getBytes();
        }
        DataBuffer bodyDateBuffer = response.bufferFactory().wrap(dataBytes);
        return response.writeWith(Mono.just(bodyDateBuffer));
    }
}