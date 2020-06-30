package com.xingyutang.zhonghe.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.exception.RequestException;
import com.xingyutang.ruihong.entity.RuihongAppointment;
import com.xingyutang.ruihong.service.RuihongAppointmentService;
import com.xingyutang.zhonghe.entity.ZhongheSplendidUser;
import com.xingyutang.zhonghe.service.ZhongheSplendidUserService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping("/api/zhonghe/sp")
public class ZhongheSplendidController {
    private final static Logger logger = LoggerFactory.getLogger(ZhongheSplendidController.class);
    private Date startDate;
    private Date endDate;

    public ZhongheSplendidController() throws ParseException {
        startDate = DateUtils.parseDate("2020-07-01", "yyyy-MM-dd");
        endDate = DateUtils.parseDate("2020-07-05", "yyyy-MM-dd");
    }
    @Autowired
    private ZhongheSplendidUserService zhongheSplendidUserService;

    @PostMapping("/appointment")
    public ResponseData makeAppointment(@RequestBody ZhongheSplendidUser user) {
        ZhongheSplendidUser entity = zhongheSplendidUserService.getUserByWxOpenId(user.getWxOpenId());
        if (entity != null) {
            return ResponseData.error(1, "你已经预约过了");
        }

        if (user.getVisitDate() == null) {
            return ResponseData.error(1, "请选择访问日期");
        }

        if (user.getVisitDate().getTime() < startDate.getTime() || user.getVisitDate().getTime() > endDate.getTime()) {
            return ResponseData.error(1, "访问日期不正确");
        }

        if (user.getVisitHour() == null) {
            return ResponseData.error(1, "请选择访问时间");
        }

        if (user.getVisitHour() < 9 || user.getVisitHour() > 18) {
            return ResponseData.error(1, "请选择早上9点到下午6点的时间段");
        }

        try {
            return ResponseData.ok(zhongheSplendidUserService.makeAppointment(user));
        } catch (RequestException e) {
            return ResponseData.error(e.getCode(), e.getMessage());
        }
    }

    @GetMapping("/appointment")
    public ResponseData getUser(@RequestParam String wxOpenId) {
        return ResponseData.ok(zhongheSplendidUserService.getUserByWxOpenId(wxOpenId));
    }
}
