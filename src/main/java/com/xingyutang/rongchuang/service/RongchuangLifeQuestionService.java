package com.xingyutang.rongchuang.service;

import com.xingyutang.rongchuang.model.entity.RongchuangLifeQuestion;

public interface RongchuangLifeQuestionService {
    RongchuangLifeQuestion selectByUserId(Long userId);

    RongchuangLifeQuestion saveQuestionResult(Long userId, RongchuangLifeQuestion question);
}
