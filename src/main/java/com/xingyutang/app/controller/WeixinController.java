package com.xingyutang.app.controller;

import com.xingyutang.app.model.entity.User;
import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.app.model.vo.UserVO;
import com.xingyutang.app.model.vo.WxUser;
import com.xingyutang.app.model.vo.WxUserToken;
import com.xingyutang.app.service.UserService;
import com.xingyutang.app.service.WeixinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/api")
public class WeixinController {
    private Logger logger		= LoggerFactory.getLogger(WeixinController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private WeixinService weixinService;

    @GetMapping("/weixinCallback")
    public Object wxLogin(String code, String status, @RequestParam(required = false) String redirectUrl, HttpSession session) {
        try {
            WxUserToken token = weixinService.getUserToken(code);
            User user = userService.getUserByOpenId(token.getOpenId());
            if (user == null) {
                WxUser wxUser = weixinService.getUserInfo(token.getAccessToken(), token.getOpenId());
                user = new User();
                user.setWxNickName(wxUser.getNickname());
                user.setWxOpenId(wxUser.getOpenId());
                user.setWxHeadImg(wxUser.getHeadImgUrl());
                user.setCreateTime(new Date());
                userService.createUser(user);

                logger.info("user created {}", user);
            }

            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUserName(user.getUserName());
            userVO.setWxNickName(user.getWxNickName());

            session.setAttribute("user", userVO);
        } catch (Exception e) {
            logger.error("failed to do weixin login ", e);
        }
        ModelAndView mv = new ModelAndView();
        if (redirectUrl != null) {
            mv.setViewName("redirect:" + redirectUrl);
        } else {
            mv.setViewName("index");
        }
        return mv;
    }

    @GetMapping("/wxJSConfig")
    @ResponseBody
    public ResponseData wxJSConfig(@RequestParam String requestURL) {
        return ResponseData.ok(weixinService.getWxConfig(requestURL));
    }

}
