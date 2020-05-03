package com.xingyutang.rongchuang.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.app.model.vo.UserVO;
import com.xingyutang.rongchuang.model.entity.RongchuangSeasonPlay;
import com.xingyutang.rongchuang.service.RongchuangSeasonPlayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;

@RestController
@RequestMapping("/api/rongchuang/season")
public class RongchuangSeasonController {
    private final static Logger logger = LoggerFactory.getLogger(RongchuangSeasonController.class);

    @Autowired
    private RongchuangSeasonPlayService seasonPlayService;

    @PostMapping("/upload")
    public ResponseData uploadAudio(@RequestParam MultipartFile file, HttpSession session) {
        UserVO userVO = (UserVO) session.getAttribute("user");
        try {
            return ResponseData.ok(seasonPlayService.savePlay(userVO.getId(), file));
        } catch (Exception e) {
            logger.error("upload audio file error:", e);
            return ResponseData.error(1, e.getMessage());
        }
    }

    @GetMapping("/saveVoice")
    public ResponseData saveWxVoice(@RequestParam String serverId, HttpSession session) {
        UserVO userVO = (UserVO) session.getAttribute("user");
        try {
            return ResponseData.ok(seasonPlayService.saveVoice(userVO.getId(), serverId));
        } catch (Exception e) {
            logger.error("save weixin audio file error:", e);
            return ResponseData.error(1, e.getMessage());
        }
    }

    @GetMapping("/myAudio")
    public ResponseEntity<InputStreamSource> getAudioFile(HttpSession session) {
        UserVO userVO = (UserVO) session.getAttribute("user");
        RongchuangSeasonPlay play = seasonPlayService.selectByUserId(userVO.getId());
        if (play == null) {
            return null;
        }
        File file = seasonPlayService.getAudioFileByPath(play.getAudioFile());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }

    @GetMapping("/userAudio")
    public ResponseEntity<InputStreamSource> getAudioFileByUserId(@RequestParam Long userId) {
        RongchuangSeasonPlay play = seasonPlayService.selectByUserId(userId);
        if (play == null) {
            return null;
        }
        File file = seasonPlayService.getAudioFileByPath(play.getAudioFile());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new FileSystemResource(file));
    }
}
