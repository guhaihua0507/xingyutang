package com.xingyutang.foliday.service;

import com.xingyutang.foliday.entity.FolidayGame;
import com.xingyutang.foliday.entity.FolidayGameAward;
import com.xingyutang.foliday.vo.FolidayUserVo;
import org.springframework.transaction.annotation.Transactional;

public interface FolidayGameService {

    FolidayGame signIn(FolidayUserVo userVo);

    FolidayGame getUserGameByUserId(String userId);

    int gainCard(Long id);

    FolidayGame getUserGameById(Long id);

    void addCoinByUser(Long userGameId, String userId);

    void updateUserGame(FolidayGame userGame);

    void claimAward(FolidayGameAward gameAward);

    FolidayGameAward getAwardByGameId(Long userGameId);
}
