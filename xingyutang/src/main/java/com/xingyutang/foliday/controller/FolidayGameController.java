package com.xingyutang.foliday.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.foliday.entity.FolidayGame;
import com.xingyutang.foliday.entity.FolidayGameAward;
import com.xingyutang.foliday.service.FolidayGameService;
import com.xingyutang.foliday.vo.ClaimAwardVo;
import com.xingyutang.foliday.vo.CoinVo;
import com.xingyutang.foliday.vo.FolidayUserVo;
import com.xingyutang.foliday.vo.GainCardVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/foliday")
public class FolidayGameController {
    private final static Logger logger = LoggerFactory.getLogger(FolidayGameController.class);

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

    @GetMapping("/startGame")
    public ResponseData startGame(@RequestParam Long id) {
        FolidayGame userGame = folidayGameService.getUserGameById(id);
        if (userGame.getCoin() <= 0) {
            return ResponseData.error(1, "你没有游戏次数了");
        }
        userGame.setCoin(userGame.getCoin() - 1);
        folidayGameService.updateUserGame(userGame);
        return ResponseData.ok();
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

    @PostMapping("/claimAward")
    public ResponseData claimAward(@RequestBody ClaimAwardVo claimAwardVo) {
        claimAwardVo.setCard1(Optional.ofNullable(claimAwardVo.getCard1()).orElse(0));
        claimAwardVo.setCard2(Optional.ofNullable(claimAwardVo.getCard2()).orElse(0));
        claimAwardVo.setCard3(Optional.ofNullable(claimAwardVo.getCard3()).orElse(0));
        claimAwardVo.setCard4(Optional.ofNullable(claimAwardVo.getCard4()).orElse(0));
        if (claimAwardVo.getCard1() < 0
                || claimAwardVo.getCard2() < 0
                || claimAwardVo.getCard3() < 0
                || claimAwardVo.getCard4() < 0) {
            return ResponseData.error(1, "参数不合法");
        }

        FolidayGameAward gameAward = new FolidayGameAward();
        gameAward.setUserGameId(claimAwardVo.getId());
        gameAward.setCard1(claimAwardVo.getCard1());
        gameAward.setCard2(claimAwardVo.getCard2());
        gameAward.setCard3(claimAwardVo.getCard3());
        gameAward.setCard4(claimAwardVo.getCard4());
        gameAward.setAwardType(claimAwardVo.getAwardType());
        gameAward.setCreateTime(new Date());
        try {
            folidayGameService.claimAward(gameAward);
            return ResponseData.ok();
        } catch (Exception e) {
            logger.error("兑奖失败", e);
            return ResponseData.error(1, e.getMessage() == null ? "兑奖失败" : e.getMessage());
        }
    }
}
