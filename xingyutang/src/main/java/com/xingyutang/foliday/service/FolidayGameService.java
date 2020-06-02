package com.xingyutang.foliday.service;

import com.xingyutang.foliday.entity.FolidayGame;
import com.xingyutang.foliday.entity.FolidayGameAward;
import com.xingyutang.foliday.vo.FolidayUserVo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FolidayGameService {

    FolidayGame signIn(FolidayUserVo userVo);

    FolidayGame getUserGameByUserId(String userId);

    int gainCard(Long id);

    FolidayGame getUserGameById(Long id);

    void addCoinByUser(Long userGameId, String userId);

    void updateUserGame(FolidayGame userGame);

    void claimAward(FolidayGameAward gameAward);

    FolidayGameAward getAwardByGameId(Long userGameId);

    List<FolidayGame> listAllUserGames();

    InputStream exportAll() throws IOException;
}
