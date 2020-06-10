package com.xingyutang.dahua.mapper;

import com.xingyutang.dahua.entity.SpringCityUser;
import com.xingyutang.foliday.entity.FolidayGame;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SpringCityUserMapper extends tk.mybatis.mapper.common.Mapper<SpringCityUser> {
    @Update("update t_dahua_spring_city_user set power=power+1 where id = #{userId}")
    void addPowerByUserId(Long userId);
}
