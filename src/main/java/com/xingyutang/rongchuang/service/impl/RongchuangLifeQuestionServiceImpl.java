package com.xingyutang.rongchuang.service.impl;

import com.xingyutang.rongchuang.mapper.RongchuangLifeQuestionMapper;
import com.xingyutang.rongchuang.model.entity.RongchuangLifeQuestion;
import com.xingyutang.rongchuang.model.entity.RongchuangSeasonPlay;
import com.xingyutang.rongchuang.service.RongchuangLifeQuestionService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

@Service
public class RongchuangLifeQuestionServiceImpl implements RongchuangLifeQuestionService {
    private Logger logger		= LoggerFactory.getLogger(RongchuangLifeQuestionServiceImpl.class);

    @Autowired
    private RongchuangLifeQuestionMapper lifeQuestionMapper;

    @Override
    public RongchuangLifeQuestion selectByUserId(Long userId) {
        Condition condition = new Condition(RongchuangSeasonPlay.class);
        condition.createCriteria().andEqualTo("userId", userId);
        condition.setOrderByClause("create_date desc");

        List<RongchuangLifeQuestion> dataList = lifeQuestionMapper.selectByExample(condition);
        if (CollectionUtils.isNotEmpty(dataList)) {
            return dataList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public RongchuangLifeQuestion saveQuestionResult(Long userId, RongchuangLifeQuestion question) {
        RongchuangLifeQuestion entity = selectByUserId(userId);
        if (entity != null) {
            entity.setResult(question.getResult());
            entity.setUpdateDate(new Date());
            lifeQuestionMapper.updateByPrimaryKey(entity);
        } else {
            entity = new RongchuangLifeQuestion();
            entity.setUserId(userId);
            entity.setResult(question.getResult());
            entity.setCreateDate(new Date());
            entity.setUpdateDate(new Date());
            lifeQuestionMapper.insert(entity);
        }

        return entity;
    }
}
