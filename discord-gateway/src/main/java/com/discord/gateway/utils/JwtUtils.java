package com.discord.gateway.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.asymmetric.RSA;
import com.discord.gateway.config.RSAConfig;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 关于JWT操作的工具类
 */
@Component
public class JwtUtils {

    private final RSAConfig rsaConfig;

    private final int time = 720;

    public JwtUtils(RSAConfig rsaConfig) {
        this.rsaConfig = rsaConfig;
    }

    /**
     * 生成token
     *
     * @param claims           存入到token中的数据
     * @return
     */
    public String createToken(Map<String, Object> claims) {
        RSA rsa = new RSA(rsaConfig.getPrivateStr(), null);

        Map<String, Object> header = new HashMap<>(16);
        header.put(JwsHeader.TYPE, JwsHeader.JWT_TYPE);
        header.put(JwsHeader.ALGORITHM, "RS256");

        // 生成token
        return Jwts.builder()
                .setHeader(header)  //header，可省略
                .setClaims(claims) //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .signWith(SignatureAlgorithm.RS256, rsa.getPrivateKey()) //通过RSA的私钥加密
                .setExpiration(DateUtil.offsetHour(new Date(), time)) //设置过期时间，单位：小时
                .compact();
    }

    /**
     * 校验token
     *
     * @param token
     * @return token中的数据，如果token失效或非法，返回null
     */
    public Map<String, Object> checkToken(String token) {
        RSA rsa = new RSA(null, rsaConfig.getPublishStr());
        try {
            // 通过token解析数据
            return Jwts.parser()
                    .setSigningKey(rsa.getPublicKey()) //通过公钥校验
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            //System.out.println("token已经过期！");
        } catch (Exception e) {
            //System.out.println("token不合法！");
            //自行打印异常信息
        }
        return null;
    }

    public String getToken(Long userId) {
        Map<String, Object> claims = new HashMap<>(16);
        claims.put("userId", Constant.USER_REDIS_PREFIX + userId);
        return createToken(claims);
    }

    public String checkTokenToString(String token) {
        Map<String, Object> map = checkToken(token);
        if (CollUtil.isNotEmpty(map)) {
            return String.valueOf(map.get("userId"));
        }
        return null;
    }

}
