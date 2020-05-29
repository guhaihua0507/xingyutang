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
        return ResponseData.ok(folidayGameService.gainCard(gainCardVo.getId()));
    }

    @PostMapping("/addCoin")
    public ResponseData addCoinByUser(@RequestBody CoinVo coinVo) {
        folidayGameService.addCoinByUser(coinVo.getId(), coinVo.getUserId());
        return ResponseData.ok();
    }

    @GetMapping("/award")
    public ResponseData showAward(@RequestParam Long id) {
        return ResponseData.ok(folidayGameService.getAwardByGameId(id));
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

        FolidayGameAward award = folidayGameService.getAwardByGameId(claimAwardVo.getId());
        if (award != null) {
            return ResponseData.error(1, "你已经兑换过奖品");
        }

        if (claimAwardVo.getAwardType() == null) {
            return ResponseData.error(1, "没有选择奖品");
        }

        //validate award
        if (claimAwardVo.getAwardType() == 1) {
            int n = 0;
            for (int i = 0; i < 4; i++) {
                if (getCard(claimAwardVo, i + 1) == 1) {
                    n += 1;
                }
            }

            if (n != 2) {
                return ResponseData.error(1, "必须用2张不同的卡片兑换该奖品");
            }
        } else if (claimAwardVo.getAwardType() == 2) {
            if (!(claimAwardVo.getCard1() == 1 && claimAwardVo.getCard2() == 1)) {
                return ResponseData.error(1, "必须使用复游两张卡片兑换");
            }
        } else if (claimAwardVo.getAwardType() == 3) {
            if (!(claimAwardVo.getCard1() == 1 && claimAwardVo.getCard2() == 1 && claimAwardVo.getCard3() == 1)) {
                return ResponseData.error(1, "必须使用复游城三张卡片兑换");
            }
        } else if (claimAwardVo.getAwardType() == 4) {
            if (!(claimAwardVo.getCard1() == 1
                    && claimAwardVo.getCard2() == 1
                    && claimAwardVo.getCard3() == 1
                    && claimAwardVo.getCard4() == 1)) {
                return ResponseData.error(1, "必须使用复游城+品牌logo四张卡片兑换");
            }
        } else {
            return ResponseData.error(1, "不存在的奖品类型:" + claimAwardVo.getAwardType());
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

    private int getCard(ClaimAwardVo claimAwardVo, int cardIndex) {
        if (cardIndex == 1) {
            return claimAwardVo.getCard1();
        }
        if (cardIndex == 2) {
            return claimAwardVo.getCard2();
        }
        if (cardIndex == 3) {
            return claimAwardVo.getCard3();
        }
        if (cardIndex == 4) {
            return claimAwardVo.getCard4();
        }
        throw new IllegalArgumentException("卡片参数错误");
    }
}
