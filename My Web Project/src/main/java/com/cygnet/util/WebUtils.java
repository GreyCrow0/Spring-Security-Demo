package com.cygnet.util;

import cn.hutool.json.JSONUtil;
import com.cygnet.domain.enums.ErrorEnum;
import com.cygnet.domain.result.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;

@Slf4j
@Component
public class WebUtils {

    public static void returnFrontendResponse(HttpServletResponse response, ErrorEnum errorEnum)  {
        try(Writer writer = response.getWriter()) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Result<String> result = Result.error(errorEnum);
            String jsonResponse = JSONUtil.toJsonStr(result);
            writer.write(jsonResponse);
            } catch (IOException e) {
                log.error("返回响应失败", e);
            }
    }
}
