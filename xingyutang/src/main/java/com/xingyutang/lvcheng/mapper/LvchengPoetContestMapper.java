package com.xingyutang.lvcheng.mapper;

import com.xingyutang.lvcheng.model.entity.LvchengPoetContest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LvchengPoetContestMapper extends tk.mybatis.mapper.common.Mapper<LvchengPoetContest> {

    @Select("select id, user_id, nick_name, score, used_time, create_time from t_lvcheng_poet_contest order by score desc, used_time asc, create_time asc limit 10")
    public List<LvchengPoetContest> selectTop10Ranking();
}
