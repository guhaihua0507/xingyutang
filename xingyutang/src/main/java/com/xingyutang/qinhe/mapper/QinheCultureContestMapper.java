package com.xingyutang.qinhe.mapper;

import com.xingyutang.qinhe.model.entity.QinheCultureContest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface QinheCultureContestMapper extends tk.mybatis.mapper.common.Mapper<QinheCultureContest> {
    @Update("update t_qinhe_culture_contest set vote=vote+1 where id=#{id}")
    void incrementVote(Long id);

    @Select("SELECT * FROM t_qinhe_culture_contest t " +
            " WHERE type = #{type} " +
            "   AND exists (SELECT 1 FROM t_qinhe_culture_file f WHERE f.contest_id = t.id) " +
            " order by t.vote desc ")
    List<QinheCultureContest> selectRankingListByType(int type);

    @Select("SELECT * FROM t_qinhe_culture_contest t " +
            " WHERE exists (SELECT 1 FROM t_qinhe_culture_file f WHERE f.contest_id = t.id) ")
    List<QinheCultureContest> selectAllWorks();
}
