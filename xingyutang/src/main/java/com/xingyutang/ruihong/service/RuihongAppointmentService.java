package com.xingyutang.ruihong.service;

import com.xingyutang.ruihong.entity.RuihongAppointment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface RuihongAppointmentService {
    RuihongAppointment save(RuihongAppointment appointment);

    List<RuihongAppointment> listRanking();

    RuihongAppointment getByUserId(String userId);

    List<RuihongAppointment> listAll();

    InputStream exportAll() throws IOException;
}
