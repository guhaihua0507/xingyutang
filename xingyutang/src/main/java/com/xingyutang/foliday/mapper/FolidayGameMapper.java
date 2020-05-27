package com.xingyutang.foliday.mapper;

import com.xingyutang.foliday.entity.FolidayGame;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FolidayGameMapper extends tk.mybatis.mapper.common.Mapper<FolidayGame> {
    @Update("update t_foliday_game set ${cardColumn}=${cardColumn} + 1 where id = #{id}")
    void addCard(Long id, String cardColumn);

    @Update("update t_foliday_game set coin=coin+1 where id = #{userGameId}")
    void addCoinByUser(Long userGameId);
}
