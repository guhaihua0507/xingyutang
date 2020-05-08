package com.xingyutang.lvcheng.service;

import com.xingyutang.lvcheng.model.entity.LvchengPoetContest;
import com.xingyutang.rongchuang.model.entity.QuizAnswer;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface LvchengPoetContestService {
    @Transactional
    LvchengPoetContest updateContestResult(LvchengPoetContest contest);

    LvchengPoetContest getContestResultByUserId(String userId);

    List<LvchengPoetContest> listRanking();

    List<LvchengPoetContest> listAll();
    List<QuizAnswer> listQuizAll();

    InputStream exportAll() throws IOException;

    int insert(QuizAnswer quizAnswer);

    int updateByPrimaryKey(QuizAnswer quizAnswer);

    boolean existsWithPrimaryKey(String userId);
}
