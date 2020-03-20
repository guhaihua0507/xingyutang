package com.xingyutang.lvcheng.service;

import com.xingyutang.lvcheng.model.entity.LvchengPoetContest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LvchengPoetContestService {
    @Transactional
    LvchengPoetContest updateContestResult(LvchengPoetContest contest);

    LvchengPoetContest getContestResultByUserId(String userId);

    List<LvchengPoetContest> listRanking();
}
