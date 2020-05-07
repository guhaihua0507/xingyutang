package com.xingyutang.rongchuang.mapper;

import com.xingyutang.rongchuang.model.entity.RongchuangLifeQuestion;
import com.xingyutang.rongchuang.model.entity.RongchuangSeasonPlay;
import com.xingyutang.rongchuang.model.vo.LifeQuestionResultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RongchuangLifeQuestionMapper extends tk.mybatis.mapper.common.Mapper<RongchuangLifeQuestion> {
    @Select("select q.id, u.id as user_id, u.wx_nick_name, q.result " +
            "  from t_rongchuang_life_question q, t_user u " +
            " where q.user_id = u.id " +
            " order by q.update_date desc")
    List<LifeQuestionResultVo> listAllResult();
}
