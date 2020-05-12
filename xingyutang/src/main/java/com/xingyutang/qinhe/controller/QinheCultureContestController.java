package com.xingyutang.qinhe.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.qinhe.model.entity.QinheCultureContest;
import com.xingyutang.qinhe.model.vo.VoteVO;
import com.xingyutang.qinhe.service.QinheCultureContestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/qinhe/culture")
public class QinheCultureContestController {
    @Autowired
    private QinheCultureContestService cultureContestService;

    @PostMapping("/signup")
    public ResponseData signUp(@RequestBody QinheCultureContest contest) {
        if (StringUtils.isBlank(contest.getUserId())) {
            return ResponseData.error(1, "没有用户id");
        }
        if (contest.getType() == null) {
            return ResponseData.error(1, "没有选择比赛类型");
        }
        return ResponseData.ok(cultureContestService.signUp(contest));
    }

    @PostMapping("/upload")
    public ResponseData uploadWork(@RequestParam("file") MultipartFile[] files, @RequestParam("id") Long id) throws IOException {
        if (files == null || files.length == 0) {
            return ResponseData.error(1, "没有文件上传");
        }
        for (MultipartFile file : files) {
            cultureContestService.saveWork(id, file);
        }
        return ResponseData.ok();
    }

    @GetMapping("/getUserWorkByType")
    public ResponseData getContestWorkByUserId(@RequestParam String userId, @RequestParam int type) {
        QinheCultureContest contest = cultureContestService.getContestByUserId(userId, type);
        if (contest != null) {
            contest.setFiles(cultureContestService.getContestWorkFiles(contest.getId()));
        }
        return ResponseData.ok(contest);
    }

    @GetMapping("/getWorkById")
    public ResponseData getWorkById(@RequestParam Long id) {
        QinheCultureContest contest = cultureContestService.getContestById(id);
        if (contest != null) {
            contest.setFiles(cultureContestService.getContestWorkFiles(contest.getId()));
        }
        return ResponseData.ok(contest);
    }

    @PostMapping("/vote")
    public ResponseData vote(@RequestBody VoteVO voteVO) {
        QinheCultureContest contest = cultureContestService.getContestById(voteVO.getId());

        int validate = cultureContestService.validateVote(voteVO.getId(), contest.getType(), voteVO.getUserId());
        if (validate == 1) {
            return ResponseData.error(1, "你已经投过票");
        }
        if (validate == 2) {
            return ResponseData.error(1, "你已经达到投票上限");
        }

        cultureContestService.vote(voteVO.getId(), voteVO.getUserId(), contest.getType());
        return ResponseData.ok();
    }

    @GetMapping("/rankingList")
    public ResponseData list() {
        return ResponseData.ok(cultureContestService.listAllWorks());
    }
}
