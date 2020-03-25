package com.xingyutang.lvcheng.controller;

import com.xingyutang.lvcheng.model.entity.LvchengPoetContest;
import com.xingyutang.lvcheng.service.LvchengPoetContestService;
import com.xingyutang.app.model.vo.ResponseData;
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
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/lvcheng/poet")
public class LvchengPoetContestController {
    private final static Logger logger = LoggerFactory.getLogger(LvchengPoetContestController.class);

    @Autowired
    private LvchengPoetContestService lvchengPoetContestService;

    @PostMapping("/contest")
    public ResponseData updateContestResult(@RequestBody LvchengPoetContest contest) {
        try {
            return ResponseData.ok(lvchengPoetContestService.updateContestResult(contest));
        } catch (Exception e) {
            logger.error("error updating contest ", e);
            return ResponseData.error(1, e.getMessage());
        }
    }

    @GetMapping("/rankingList")
    public ResponseData listRanking() {
        try {
            List<LvchengPoetContest> list = lvchengPoetContestService.listRanking();
            return ResponseData.ok(list);
        } catch (Exception e) {
            logger.error("error listing ranking ", e);
            return ResponseData.error(1, e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseData listAll() {
        try {
            List<LvchengPoetContest> dataList = lvchengPoetContestService.listAll();
            return ResponseData.ok(dataList);
        } catch (Exception e) {
            logger.error("error listing all data ", e);
            return ResponseData.error(1, e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamSource> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "poet_all.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(lvchengPoetContestService.exportAll()));
    }
}
