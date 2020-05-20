package com.xingyutang.ruihong.service;

import com.xingyutang.ruihong.entity.RuihongAppointment;

import java.util.List;

public interface RuihongAppointmentService {
    RuihongAppointment save(RuihongAppointment appointment);

    List<RuihongAppointment> listRanking();
}
