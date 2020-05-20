package com.xingyutang.ruihong.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.ruihong.entity.RuihongAppointment;
import com.xingyutang.ruihong.service.RuihongAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ruihong/appointment")
public class RuihongAppointmentController {
    @Autowired
    private RuihongAppointmentService appointmentService;

    @PostMapping("/save")
    public ResponseData save(@RequestBody RuihongAppointment appointment) {
        return ResponseData.ok(appointmentService.save(appointment));
    }

    @GetMapping("/ranking")
    public ResponseData listRanking() {
        return ResponseData.ok(appointmentService.listRanking());
    }
}
