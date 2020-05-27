package com.xingyutang.foliday.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.foliday.entity.FolidayGame;
import com.xingyutang.foliday.service.FolidayGameService;
import com.xingyutang.foliday.vo.CoinVo;
import com.xingyutang.foliday.vo.FolidayUserVo;
import com.xingyutang.foliday.vo.GainCardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/foliday")
public class FolidayGameController {
    @Autowired
    private FolidayGameService folidayGameService;

    @GetMapping("/userGame")
    public ResponseData getUserGame(@RequestParam String userId) {
        FolidayGame userGame = folidayGameService.getUserGameByUserId(userId);
        return ResponseData.ok(userGame);
    }

    @PostMapping("/signIn")
    public ResponseData signIn(@RequestBody FolidayUserVo userVo) {
        FolidayGame userGame = folidayGameService.getUserGameByUserId(userVo.getUserId());
        if (userGame != null) {
            return ResponseData.error(1, "你已经注册过了");
        }
        return ResponseData.ok(folidayGameService.signIn(userVo));
    }

    @PostMapping("/gainCard")
    public ResponseData addCard(@RequestBody GainCardVo gainCardVo) {
        return ResponseData.ok(folidayGameService.gainCard(gainCardVo.getId(), gainCardVo.getCard()));
    }

    @PostMapping("/addCoin")
    public ResponseData addCoinByUser(@RequestBody CoinVo coinVo) {
        folidayGameService.addCoinByUser(coinVo.getId(), coinVo.getUserId());
        return ResponseData.ok();
    }
}
