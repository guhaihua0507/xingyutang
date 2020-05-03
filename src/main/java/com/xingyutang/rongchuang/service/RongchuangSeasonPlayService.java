package com.xingyutang.rongchuang.service;

import com.xingyutang.rongchuang.model.entity.RongchuangSeasonPlay;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface RongchuangSeasonPlayService {
    RongchuangSeasonPlay selectByUserId(Long userId);

    RongchuangSeasonPlay selectById(Long id);

    RongchuangSeasonPlay savePlay(Long userId, MultipartFile file) throws IOException;

    RongchuangSeasonPlay saveVoice(Long userId, String serverId) throws IOException;

    File getAudioFile(Long id);

    File getAudioFileByPath(String audioFile);
}
