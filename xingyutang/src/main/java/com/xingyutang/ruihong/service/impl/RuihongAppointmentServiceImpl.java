package com.xingyutang.ruihong.service.impl;

import com.xingyutang.ruihong.entity.RuihongAppointment;
import com.xingyutang.ruihong.mapper.RuihongAppointmentMapper;
import com.xingyutang.ruihong.service.RuihongAppointmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

@Service
public class RuihongAppointmentServiceImpl implements RuihongAppointmentService {
    @Autowired
    private RuihongAppointmentMapper appointmentMapper;

    @Override
    public RuihongAppointment save(RuihongAppointment appointment) {
        RuihongAppointment entity = getByUserId(appointment.getUserId());
        if (entity == null) {
            appointment.setCreateTime(new Date());
            appointment.setId(null);
            appointmentMapper.insert(appointment);
        } else {
            if (appointment.getKawsNumber() > entity.getKawsNumber()) {
                appointment.setId(entity.getId());
                appointment.setCreateTime(new Date());
                appointmentMapper.updateByPrimaryKey(appointment);
            }
        }
        return appointment;
    }


    @Override
    public List<RuihongAppointment> listRanking() {
        Condition condition = new Condition(RuihongAppointment.class);
        condition.setOrderByClause("kaws_number desc, create_time");

        List<RuihongAppointment> dataList = appointmentMapper.selectByExample(condition);
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                RuihongAppointment appointment = dataList.get(i);
                appointment.setRanking(i + 1);
            }
        }
        return dataList;
    }

    public RuihongAppointment getByUserId(String userId) {
        Condition condition = new Condition(RuihongAppointment.class);
        condition.createCriteria().andEqualTo("userId", userId);
        List<RuihongAppointment> dataList = appointmentMapper.selectByExample(condition);
        if (dataList != null && dataList.size() > 0) {
            return dataList.get(0);
        }
        return null;
    }
}
