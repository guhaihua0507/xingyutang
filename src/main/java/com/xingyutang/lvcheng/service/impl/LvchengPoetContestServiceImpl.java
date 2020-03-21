package com.xingyutang.lvcheng.service.impl;

import com.xingyutang.lvcheng.mapper.LvchengPoetContestMapper;
import com.xingyutang.lvcheng.model.entity.LvchengPoetContest;
import com.xingyutang.lvcheng.service.LvchengPoetContestService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

@Service
public class LvchengPoetContestServiceImpl implements LvchengPoetContestService {
    private final static Logger logger = LoggerFactory.getLogger(LvchengPoetContestServiceImpl.class);
    @Autowired
    private LvchengPoetContestMapper lvchengPoetContestMapper;

    @Override
    @Transactional
    public LvchengPoetContest updateContestResult(LvchengPoetContest contest) {
        fillDefault(contest);
        contest.setCreateTime(new Date());
        LvchengPoetContest entity = getContestResultByUserId(contest.getUserId());

        if (entity == null) {
            lvchengPoetContestMapper.insert(contest);
            return contest;
        }

        fillDefault(entity);
        contest.setId(entity.getId());
        if (entity.getScore() > contest.getScore()
                || (entity.getScore() == contest.getScore() && entity.getUsedTime() <= contest.getUsedTime())) {
            return entity;
        } else {
            lvchengPoetContestMapper.updateByPrimaryKey(contest);
            return contest;
        }
    }

    private void fillDefault(LvchengPoetContest contest) {
        if (contest.getScore() == null) {
            contest.setScore(0);
        }
        if (contest.getUsedTime() == null) {
            contest.setUsedTime(0);
        }
    }

    @Override
    public LvchengPoetContest getContestResultByUserId(String userId) {
        Condition condition = new Condition(LvchengPoetContest.class);
        condition.createCriteria().andEqualTo("userId", userId);
        List<LvchengPoetContest> resultList =  lvchengPoetContestMapper.selectByExample(condition);
        return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
    }

    @Override
    public List<LvchengPoetContest> listRanking() {
//        Condition condition = new Condition(LvchengPoetContest.class);
//        condition.setOrderByClause("score desc, used_time asc, create_time limit 10");
//        return lvchengPoetContestMapper.selectByExample(condition);
        return lvchengPoetContestMapper.selectTop10Ranking();
    }
}
