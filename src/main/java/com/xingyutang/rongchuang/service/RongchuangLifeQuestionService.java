package com.xingyutang.rongchuang.service;

import com.xingyutang.rongchuang.model.entity.RongchuangLifeQuestion;
import com.xingyutang.rongchuang.model.vo.LifeQuestionResultVo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface RongchuangLifeQuestionService {
    RongchuangLifeQuestion selectByUserId(Long userId);

    RongchuangLifeQuestion saveQuestionResult(Long userId, RongchuangLifeQuestion question);

    List<LifeQuestionResultVo> listAll();

    InputStream exportAll() throws IOException;
}
