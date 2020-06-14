package com.xingyutang.qinhe.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.qinhe.model.entity.QinheCultureContest;
import com.xingyutang.qinhe.model.entity.QinheCultureFile;
import com.xingyutang.qinhe.model.vo.RankingVO;
import com.xingyutang.qinhe.model.vo.VoteVO;
import com.xingyutang.qinhe.service.QinheCultureContestService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import com.github.pagehelper.PageHelper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/qinhe/culture")
public class QinheCultureContestController {
    private final static Logger logger = LoggerFactory.getLogger(QinheCultureContestController.class);
    @Autowired
    private QinheCultureContestService cultureContestService;

    @PostMapping("/signup")
    public ResponseData signUp(@RequestBody QinheCultureContest contest, @RequestParam(defaultValue = "false") boolean forceNew) {
        if (StringUtils.isBlank(contest.getUserId())) {
            return ResponseData.error(1, "没有用户id");
        }
        if (contest.getType() == null) {
            return ResponseData.error(1, "没有选择比赛类型");
        }

        if (forceNew) {
            logger.info("force to sign new user {}", contest);
            return ResponseData.ok(cultureContestService.signUp(contest));
        } else {
            QinheCultureContest entity = cultureContestService.getContestByUserId(contest.getUserId(), contest.getType());
            if (entity == null) {
                logger.info("user not exist, sign as new user userId={}, type={}", contest.getUserId(), contest.getType());
                return ResponseData.ok(cultureContestService.signUp(contest));
            } else {
                logger.info("update user: userId={}, type={}", contest.getUserId(), contest.getType());
                contest.setId(entity.getId());
                contest.setVote(entity.getVote());
                contest.setCreateTime(entity.getCreateTime());
                contest.setUpdateTime(new Date());
                return ResponseData.ok(cultureContestService.updateSignInfo(contest));
            }
        }
    }

    @PostMapping("/signInfo/update")
    public ResponseData updateSignInfo(@RequestBody QinheCultureContest contest) {
        if (StringUtils.isBlank(contest.getUserId())) {
            return ResponseData.error(1, "没有用户id");
        }
        if (contest.getType() == null) {
            return ResponseData.error(1, "没有选择比赛类型");
        }
        return ResponseData.ok(cultureContestService.updateSignInfo(contest));
    }

    @GetMapping("/searchWork")
    public ResponseData searchWork(@RequestParam Integer type, @RequestParam Integer playerType, @RequestParam(required = false) String name, @RequestParam(required = false) String phoneNumber) {
        if (StringUtils.isBlank(name) && StringUtils.isBlank(phoneNumber)) {
            return ResponseData.error(1, "请输入姓名或者手机号查询");
        }
        List<QinheCultureContest> dataList = cultureContestService.searchWork(type, playerType, name, phoneNumber);
        cultureContestService.loadWorkFiles(dataList);

        return ResponseData.ok(dataList);
    }

    @PostMapping("/upload")
    public ResponseData uploadWork(@RequestParam("file") MultipartFile[] files, @RequestParam("id") Long id) throws IOException {
        if (files == null || files.length == 0) {
            return ResponseData.error(1, "没有文件上传");
        }
        /*for (MultipartFile file : files) {
            System.out.println(file.getContentType());
            cultureContestService.saveWork(id, file);
        }*/
        cultureContestService.updateWorkFiles(id, files);
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
        try {
            Date voteStart = DateUtils.parseDate("2020/06/15 10:00:00", "yyyy/MM/dd HH:mm:ss");
            if (System.currentTimeMillis() < voteStart.getTime()) {
                return ResponseData.error(1, "投票还没开始");
            }
        } catch (Exception e) {
            logger.error("error check date", e);
        }
        QinheCultureContest contest = cultureContestService.getContestById(voteVO.getId());

        int validate = cultureContestService.validateVote(voteVO.getId(), contest.getType(), voteVO.getUserId());
        if (validate == 1) {
            logger.info("你已经投过票: userId={}, contestid={}", voteVO.getUserId(), voteVO.getId());
            return ResponseData.error(1, "你已经投过票");
        }
        if (validate == 2) {
            logger.info("你已经达到投票上限: userId={}, contestid={}", voteVO.getUserId(), voteVO.getId());
            return ResponseData.error(2, "你已经达到投票上限");
        }

        cultureContestService.vote(voteVO.getId(), voteVO.getUserId(), contest.getType());
        return ResponseData.ok();
    }

    @GetMapping("/workList")
    public ResponseData listWorks(@RequestParam int type, @RequestParam int playerType, @RequestParam(defaultValue = "1") int pageNo, @RequestParam(defaultValue = "10") int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<QinheCultureContest> dataList = cultureContestService.listWorksByType(type, playerType);
        PageHelper.clearPage();
        return ResponseData.ok(dataList);
    }

    @GetMapping("/ranking")
    public ResponseData listRanking(@RequestParam int type, @RequestParam int playerType, @RequestParam(defaultValue = "10") int top) {
        List<RankingVO> dataList = cultureContestService.listRankingByType(type, playerType, top);
        return ResponseData.ok(dataList);
    }

    @GetMapping("/file")
    public ResponseEntity<InputStreamSource> getFileByUserId(@RequestParam Long id) {
        QinheCultureFile cultureFile = cultureContestService.getCultureFileById(id);
        if (cultureFile == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", cultureFile.getContentType());
        headers.setContentDispositionFormData("attachment", cultureFile.getFile());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new FileSystemResource(cultureContestService.getFile(cultureFile)));
    }

    @GetMapping("/thumb_file")
    public ResponseEntity<InputStreamSource> getThumbFileByUserId(@RequestParam Long id) {
        QinheCultureFile cultureFile = cultureContestService.getCultureFileById(id);
        if (cultureFile == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", cultureFile.getContentType());
        headers.setContentDispositionFormData("attachment", cultureFile.getFile());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new FileSystemResource(cultureContestService.getThumbFile(cultureFile)));
    }

    @GetMapping("/listAll")
    public ResponseData list() {
        List<QinheCultureContest> dataList = cultureContestService.listAllWorks();
        return ResponseData.ok(dataList);
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamSource> download() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "list.xlsx");
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(cultureContestService.exportAll()));
    }

    @GetMapping("/refreshThumb")
    public ResponseData refreshThumb() {
        return ResponseData.ok(cultureContestService.generateAllThumbs());
    }

    @GetMapping("/compressImages")
    public ResponseData compressAllImages() {
        return ResponseData.ok(cultureContestService.compressAllImages());
    }
}
