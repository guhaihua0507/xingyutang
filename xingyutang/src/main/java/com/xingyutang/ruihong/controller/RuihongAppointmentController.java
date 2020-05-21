package com.xingyutang.ruihong.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.ruihong.entity.RuihongAppointment;
import com.xingyutang.ruihong.service.RuihongAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    @GetMapping("/listAll")
    public ResponseData listAll() {
        return ResponseData.ok(appointmentService.listAll());
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamSource> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "list.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(appointmentService.exportAll()));
    }
}
