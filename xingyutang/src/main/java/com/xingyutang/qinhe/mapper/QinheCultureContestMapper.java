package com.xingyutang.qinhe.mapper;

import com.xingyutang.qinhe.model.entity.QinheCultureContest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QinheCultureContestMapper extends tk.mybatis.mapper.common.Mapper<QinheCultureContest> {
    @Update("update t_qinhe_culture_contest set vote=vote+1 where id=#{id}")
    void incrementVote(Long id);
}
