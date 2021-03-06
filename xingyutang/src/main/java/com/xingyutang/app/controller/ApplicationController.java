package com.xingyutang.app.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.app.model.vo.UserVO;
import com.xingyutang.app.service.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApplicationController {
    @Autowired
    private WeixinService weixinService;

    @GetMapping("/index")
    public ModelAndView test(ModelAndView mv) {
        mv.setViewName("/index");
        return mv;
    }

    @GetMapping("/testToken")
    @ResponseBody
    public String testToken(@RequestParam(required = false) String echostr) {
        return echostr;
    }

    @GetMapping("/user")
    @ResponseBody
    public ResponseData checkUser(HttpSession session) {
        UserVO user = (UserVO) session.getAttribute("user");
        if (user == null) {
            Map<String, String> data = new HashMap<>();
            data.put("appId", weixinService.getAppId());
            return ResponseData.error(1, "请先通过微信登录", data);
        } else {
            return ResponseData.ok(user);
        }
    }

    @GetMapping("/appId")
    @ResponseBody
    public ResponseData getAppId() {
        return ResponseData.ok(weixinService.getAppId());
    }
}
