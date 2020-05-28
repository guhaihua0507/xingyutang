package com.xingyutang.foliday.service;

import com.xingyutang.foliday.entity.FolidayGame;
import com.xingyutang.foliday.entity.FolidayGameAward;
import com.xingyutang.foliday.vo.FolidayUserVo;

public interface FolidayGameService {

    FolidayGame signIn(FolidayUserVo userVo);

    FolidayGame getUserGameByUserId(String userId);

    FolidayGame gainCard(Long id, Integer card);

    FolidayGame getUserGameById(Long id);

    void addCoinByUser(Long userGameId, String userId);

    void updateUserGame(FolidayGame userGame);

    void claimAward(FolidayGameAward gameAward);
}
