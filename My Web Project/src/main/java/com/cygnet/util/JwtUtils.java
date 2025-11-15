package com.cygnet.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.cygnet.domain.enums.ErrorEnum;
import com.cygnet.exception.IException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public boolean shouldRefreshToken(String token) {
        try {
            //验证token是否完整
            if (!JWTUtil.verify(token, SECRET_KEY.getBytes())) {
                return false;
            }
            JWT jwt = JWTUtil.parseToken(token);
            //是否过期
            long exp = Long.parseLong(jwt.getPayload().getClaim("exp").toString());
           return exp * 1000 - System.currentTimeMillis() < 10 * 1000 * 60;

        } catch (Exception e) {
            log.error("token解析失败");
            throw new IException(ErrorEnum.SYSTEM_ERROR);
        }

    }

    public String createToken(Long id, Boolean rememberMe) {
        return JWT.create()
                .setExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(rememberMe?7:1)))
                .setPayload("id", id)
                .setKey(SECRET_KEY.getBytes())         // 签名密钥
                .sign();
    }

    public Long parseToken(String token) {
        try {
            //验证token是否完整
            if (!JWTUtil.verify(token, SECRET_KEY.getBytes())) {
                return null;
            }
            JWT jwt = JWTUtil.parseToken(token);
            //是否过期
            long exp = Long.parseLong(jwt.getPayload().getClaim("exp").toString());
            if (System.currentTimeMillis() > exp * 1000) {
                return null;
            }

            Object claim = jwt.getPayload().getClaim("id");
            if (claim instanceof cn.hutool.core.convert.NumberWithFormat) {
                return Convert.toLong(claim);
            }

        } catch (Exception e) {
            log.error("token解析失败");
            throw new IException(ErrorEnum.SYSTEM_ERROR);
        }
        return null;
    }

    public String refreshToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);

        Object claim = jwt.getPayload().getClaim("id");

        // 获取原始过期时间戳（秒）
        Long expSeconds = Convert.toLong(jwt.getPayload().getClaim("exp"));
        // 转换为毫秒并增加持续时间
        long newExpMillis = expSeconds * 1000 + 30 * 60 * 1000;

        return JWT.create()
                .setExpiresAt(new Date(newExpMillis))
                .setPayload("id", claim)
                .setKey(SECRET_KEY.getBytes())         // 签名密钥
                .sign();
    }
}
