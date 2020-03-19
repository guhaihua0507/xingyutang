package com.xingyutang.controller;

import com.xingyutang.model.entity.User;
import com.xingyutang.model.vo.UserVO;
import com.xingyutang.model.vo.WxUser;
import com.xingyutang.model.vo.WxUserToken;
import com.xingyutang.service.UserService;
import com.xingyutang.service.WeixinService;
import com.xingyutang.service.impl.RongchuangSupplierConventionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
//            WxUserToken token = new WxUserToken();  //TODO remove this
//            token.setOpenId("1111");    //TODO remove this
            User user = userService.getUserByOpenId(token.getOpenId());
            if (user == null) {
                //TODO create user
                WxUser wxUser = weixinService.getUserInfo(token.getAccessToken(), token.getOpenId());
                /*WxUser wxUser = new WxUser();   //TODO remove this
                wxUser.setOpenId("1111");
                wxUser.setNickname("guhaihua");
                wxUser.setSex(1);*/

                user = new User();
                user.setWxNickName(wxUser.getNickname());
                user.setWxOpenId(wxUser.getOpenId());
                user.setWxHeadImg(wxUser.getHeadImgUrl());
                user.setCreateTime(new Date());
                userService.createUser(user);

                System.out.println("new user created");
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
}
