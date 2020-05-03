package com.xingyutang.rongchuang.service.impl;

import com.xingyutang.rongchuang.mapper.RongchuangSeasonPlayMapper;
import com.xingyutang.rongchuang.model.entity.RongchuangSeasonPlay;
import com.xingyutang.rongchuang.service.RongchuangSeasonPlayService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RongchuangSeasonPlayServiceImpl implements RongchuangSeasonPlayService {
    @Autowired
    private RongchuangSeasonPlayMapper seasonPlayMapper;
    @Value("${rongchuang.season.audioPath}")
    private String audioBasePath;

    @Override
    public RongchuangSeasonPlay selectByUserId(Long userId) {
        Condition condition = new Condition(RongchuangSeasonPlay.class);
        condition.createCriteria().andEqualTo("userId", userId);
        condition.setOrderByClause("create_date desc");

        List<RongchuangSeasonPlay> dataList = seasonPlayMapper.selectByExample(condition);
        if (CollectionUtils.isNotEmpty(dataList)) {
            return dataList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public RongchuangSeasonPlay selectById(Long id) {
        return seasonPlayMapper.selectByPrimaryKey(id);
    }

    @Override
    public RongchuangSeasonPlay savePlay(Long userId, MultipartFile file) throws IOException {
        String subPath = UUID.randomUUID().toString() + "/" + file.getOriginalFilename();
        String filePath = audioBasePath + "/" + subPath;
        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(filePath));

        RongchuangSeasonPlay entity = selectByUserId(userId);
        if (entity != null) {
            entity.setAudioFile(subPath);
            entity.setUpdateDate(new Date());
            seasonPlayMapper.updateByPrimaryKey(entity);
        } else {
            entity = new RongchuangSeasonPlay();
            entity.setUserId(userId);
            entity.setAudioFile(subPath);
            entity.setCreateDate(new Date());
            entity.setUpdateDate(new Date());
            seasonPlayMapper.insert(entity);
        }

        return entity;
    }

    @Override
    public File getAudioFile(Long id) {
        RongchuangSeasonPlay play = selectById(id);
        if (play == null) {
            return null;
        }

        return getAudioFileByPath(play.getAudioFile());
    }

    @Override
    public File getAudioFileByPath(String audioFile) {
        return new File(audioBasePath + "/" + audioFile);
    }
}