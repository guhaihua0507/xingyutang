package com.xingyutang.rongchuang.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.app.model.vo.UserVO;
import com.xingyutang.exception.RequestException;
import com.xingyutang.rongchuang.model.entity.RongchuangSupplier;
import com.xingyutang.rongchuang.service.RongchuangSupplierConventionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/api/rongchuang/convention")
public class RongchuangSupplierConventionController {

    @Autowired
    private RongchuangSupplierConventionService rongchuangSupplierConventionService;

    @GetMapping("/index")
    public ModelAndView index(ModelAndView mv) {
        System.out.println("-------------------redirect");
        mv.setViewName("rongchuang/convention/index");
        return mv;
    }

    @GetMapping("/suppliers")
    @ResponseBody
    public ResponseData listSuppliers() {
        return ResponseData.ok(rongchuangSupplierConventionService.listSuppliers());
    }

    @PostMapping("/appointment")
    @ResponseBody
    public ResponseData handleAppointment(@RequestBody Map<String, String> data, HttpSession session) {
        String supplierName = data.get("supplierName");
        String name = data.get("name");
        String phoneNumber = data.get("phoneNumber");
        if (StringUtils.isBlank(supplierName)) {
            return ResponseData.error(2, "请输入供应商名称");
        }
        if (StringUtils.isBlank(name)) {
            return ResponseData.error(2, "请输入姓名");
        }
        if (StringUtils.isBlank(phoneNumber)) {
            return ResponseData.error(2, "请输入手机号");
        }

        UserVO userVO = (UserVO) session.getAttribute("user");

        RongchuangSupplier supplier = new RongchuangSupplier();
        supplier.setUserId(userVO.getId());
        supplier.setSupplierName(supplierName);
        supplier.setName(name);
        supplier.setPhoneNumber(phoneNumber);
        supplier.setCreateTime(new Date());

        rongchuangSupplierConventionService.createSupplier(supplier);

        return ResponseData.ok(supplier);
    }

    @GetMapping("/appointment")
    @ResponseBody
    public ResponseData loadAppointment(HttpSession session) {
        UserVO userVO = (UserVO) session.getAttribute("user");
        RongchuangSupplier supplier = rongchuangSupplierConventionService.getSupplierByUserId(userVO.getId());
        return ResponseData.ok(supplier);
    }

    @PostMapping("/signIn")
    @ResponseBody
    public ResponseData signIn(HttpSession session) {
        UserVO userVO = (UserVO) session.getAttribute("user");
        try {
            rongchuangSupplierConventionService.signIn(userVO.getId());
            return ResponseData.ok();
        } catch (RequestException e) {
            return ResponseData.error(e.getCode(), e.getMessage());
        }
    }

    @GetMapping("/lottery")
    @ResponseBody
    public ResponseData doLottery(HttpSession session) {
        UserVO userVO = (UserVO) session.getAttribute("user");
        try {
            int prize = rongchuangSupplierConventionService.lottery(userVO.getId());
            return ResponseData.ok(prize);
        } catch (RequestException e) {
            return ResponseData.error(e.getCode(), e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamSource> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "suppliers.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(rongchuangSupplierConventionService.exportSuppliers()));
    }
}
