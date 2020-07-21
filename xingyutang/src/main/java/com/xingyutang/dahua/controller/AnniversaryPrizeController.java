package com.xingyutang.dahua.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.dahua.entity.AnniversaryUser;
import com.xingyutang.dahua.entity.AnniversaryUserPrize;
import com.xingyutang.dahua.service.AnniversaryUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/dahua/anniversary")
public class AnniversaryPrizeController {
    @Autowired
    private AnniversaryUserService anniversaryUserService;

    @GetMapping("/user")
    public ResponseData getUserByWxOpenId(@RequestParam String wxOpenId) {
        AnniversaryUser user = anniversaryUserService.getUserByOpenId(wxOpenId);
        return ResponseData.ok(user);
    }

    @PostMapping("/signIn")
    public ResponseData signIn(@RequestBody AnniversaryUser user) {
        AnniversaryUser entity = anniversaryUserService.getUserByOpenId(user.getWxOpenId());
        if (entity != null) {
            return ResponseData.error(1, "你已经注册过了");
        }
        return ResponseData.ok(anniversaryUserService.signIn(user));
    }

    @GetMapping("/play")
    public ResponseData drawLottery(@RequestParam Long userId) {
        AnniversaryUserPrize userPrize = anniversaryUserService.getUserPrize(userId, new Date());
        if (userPrize != null) {
            return ResponseData.error(1, "你今天已经抽过奖了");
        }
        Integer prize = anniversaryUserService.drawLottery(userId);
        if (prize == null) {
            return ResponseData.error(1, "奖品已经抽完");
        }
        return ResponseData.ok(prize);
    }

    @GetMapping("/userPrize")
    public ResponseData getUserPrize(Long userId) {
        List<AnniversaryUserPrize> userPrizes = anniversaryUserService.getUserPrizeList(userId);
        return ResponseData.ok(userPrizes);
    }

    @GetMapping("/resetPrizePool")
    public ResponseData resetPrizePool() {
        anniversaryUserService.resetPrizePool();
        return ResponseData.ok();
    }
}
