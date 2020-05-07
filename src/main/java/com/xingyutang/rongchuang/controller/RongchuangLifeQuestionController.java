package com.xingyutang.rongchuang.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.app.model.vo.UserVO;
import com.xingyutang.rongchuang.model.entity.RongchuangLifeQuestion;
import com.xingyutang.rongchuang.model.entity.RongchuangSeasonPlay;
import com.xingyutang.rongchuang.service.RongchuangLifeQuestionService;
import com.xingyutang.rongchuang.service.RongchuangSeasonPlayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/rongchuang/life")
public class RongchuangLifeQuestionController {
    private final static Logger logger = LoggerFactory.getLogger(RongchuangLifeQuestionController.class);

    @Autowired
    private RongchuangLifeQuestionService lifeQuestionService;

    @PostMapping("/answer")
    public ResponseData uploadAudio(@RequestBody RongchuangLifeQuestion question, HttpSession session) {
        UserVO userVO = (UserVO) session.getAttribute("user");
        return ResponseData.ok(lifeQuestionService.saveQuestionResult(userVO.getId(), question));
    }

    @GetMapping("/listAll")
    public ResponseData listResults() {
        return ResponseData.ok(lifeQuestionService.listAll());
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamSource> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "result_list.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(lifeQuestionService.exportAll()));
    }
}
