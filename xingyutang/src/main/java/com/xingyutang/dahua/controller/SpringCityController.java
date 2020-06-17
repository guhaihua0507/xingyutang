package com.xingyutang.dahua.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.dahua.entity.SpringCityUser;
import com.xingyutang.dahua.service.SpringCityService;
import com.xingyutang.dahua.vo.ClaimAwardVo;
import com.xingyutang.dahua.vo.PowerSupportVo;
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

@RestController
@RequestMapping("/api/dahua/springCity")
public class SpringCityController {
    private final static Logger logger = LoggerFactory.getLogger(SpringCityController.class);

    @Autowired
    private SpringCityService springCityService;

    @GetMapping("/listAll")
    public ResponseData listAllUsers() {
        return ResponseData.ok(springCityService.listAllUsers());
    }

    @GetMapping("/user")
    public ResponseData getUserByWxOpenId(@RequestParam String wxOpenId) {
        SpringCityUser user = springCityService.getUserByWxOpenId(wxOpenId);
        return ResponseData.ok(user);
    }

    @PostMapping("/signIn")
    public ResponseData signIn(@RequestBody SpringCityUser user) {
        SpringCityUser entity = springCityService.getUserByWxOpenId(user.getWxOpenId());
        if (entity != null) {
            return ResponseData.error(1, "你已经注册过了");
        }
        return ResponseData.ok(springCityService.signIn(user));
    }

    @PostMapping("/user/update")
    public ResponseData updateUserInfo(@RequestBody SpringCityUser userVo) {
        return ResponseData.ok(springCityService.updateUser(userVo));
    }

    @PostMapping("/supportPower")
    public ResponseData supportPower(@RequestBody PowerSupportVo powerSupportVo) {
        springCityService.addPowerByWxUser(powerSupportVo.getUserId(), powerSupportVo.getWxOpenId());
        return ResponseData.ok();
    }

    @GetMapping("/awards")
    public ResponseData listAwards() {
        return ResponseData.ok(springCityService.getAwards());
    }

    @PostMapping("/claimAward")
    public ResponseData claimAward(@RequestBody ClaimAwardVo claimAwardVo) {
        try {
            springCityService.claimAward(claimAwardVo.getUserId(), claimAwardVo.getAwardType());
            return ResponseData.ok();
        } catch (Exception e) {
            return ResponseData.error(1, e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamSource> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "list.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(springCityService.exportAll()));
    }
}
