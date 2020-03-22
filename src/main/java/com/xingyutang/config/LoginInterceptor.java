package com.xingyutang.config;

import com.alibaba.fastjson.JSON;
import com.xingyutang.Application;
import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.app.service.WeixinService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            Map<String, String> data = new HashMap<>();
            data.put("appId", Application.getBean(WeixinService.class).getAppId());
            ResponseData respData = ResponseData.error(1, "你还没有登录", data);
            response.getWriter().append(JSON.toJSONString(respData));
            return false;
        }
        return true;
    }
}
