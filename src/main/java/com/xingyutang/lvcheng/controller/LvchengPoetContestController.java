package com.xingyutang.lvcheng.controller;

import com.xingyutang.lvcheng.model.entity.LvchengPoetContest;
import com.xingyutang.lvcheng.service.LvchengPoetContestService;
import com.xingyutang.model.vo.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
