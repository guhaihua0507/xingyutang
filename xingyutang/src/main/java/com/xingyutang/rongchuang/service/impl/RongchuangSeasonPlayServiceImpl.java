package com.xingyutang.rongchuang.service.impl;

import com.xingyutang.app.service.WeixinService;
import com.xingyutang.rongchuang.mapper.RongchuangSeasonPlayMapper;
import com.xingyutang.rongchuang.model.entity.RongchuangSeasonPlay;
import com.xingyutang.rongchuang.service.RongchuangSeasonPlayService;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class RongchuangSeasonPlayServiceImpl implements RongchuangSeasonPlayService {
    private Logger logger		= LoggerFactory.getLogger(RongchuangSeasonPlayServiceImpl.class);

    @Autowired
    private RongchuangSeasonPlayMapper seasonPlayMapper;
    @Value("${rongchuang.season.audioPath}")
    private String audioBasePath;
    @Autowired
    private WeixinService weixinService;

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
    public RongchuangSeasonPlay saveVoice(Long userId, String serverId) throws IOException {
        String subPath = UUID.randomUUID().toString();
        String amrFile = audioBasePath + "/" + subPath + "/voice.amr";
        String mp3File = audioBasePath + "/" + subPath + "/voice.mp3";
        try (InputStream in = weixinService.getVoiceInputStream(serverId)) {
            FileUtils.copyInputStreamToFile(in, new File(amrFile));
            amrToMp3ForLinux(amrFile, mp3File);
        }

        RongchuangSeasonPlay entity = selectByUserId(userId);
        if (entity != null) {
            entity.setAudioFile(subPath + "/voice.mp3");
            entity.setUpdateDate(new Date());
            seasonPlayMapper.updateByPrimaryKey(entity);
        } else {
            entity = new RongchuangSeasonPlay();
            entity.setUserId(userId);
            entity.setAudioFile(subPath + "/voice.mp3");
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

    private void amrToMp3(File amrFile, File mp3File) {
        AudioAttributes audio = new AudioAttributes();
        Encoder encoder = new Encoder();

        audio.setCodec("libmp3lame");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);
        try {
            encoder.encode(amrFile, mp3File, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void amrToMp3ForLinux(String amrFile, String mp3File) {
        String command = "ffmpeg  -i " + amrFile + " " + mp3File;
        logger.info("start to covert amr to mp3: {}", command);
        Runtime runtime = Runtime.getRuntime();
        BufferedReader br = null;
        try {
            Process proc = runtime.exec(command);
            br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            logger.info(sb.toString());
            int exitVal = proc.waitFor();
            logger.info("exit value is {}", exitVal);
        } catch (IOException e) {
            logger.error("error when convert amr to mp3", e);
        } catch (InterruptedException e) {
            logger.error("ffmpeg exec cmd Exception ", e);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }
}
