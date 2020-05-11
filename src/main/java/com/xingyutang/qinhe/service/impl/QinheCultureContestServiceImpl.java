package com.xingyutang.qinhe.service.impl;

import com.xingyutang.qinhe.mapper.QinheCultureContestMapper;
import com.xingyutang.qinhe.mapper.QinheCultureFileMapper;
import com.xingyutang.qinhe.model.entity.QinheCultureContest;
import com.xingyutang.qinhe.model.entity.QinheCultureFile;
import com.xingyutang.qinhe.service.QinheCultureContestService;
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
public class QinheCultureContestServiceImpl implements QinheCultureContestService {
    @Autowired
    private QinheCultureContestMapper cultureContestMapper;
    @Autowired
    private QinheCultureFileMapper cultureFileMapper;

    @Value("${qinhe.file.path}")
    private String basePath;

    @Override
    public QinheCultureContest signUp(QinheCultureContest contest) {
        contest.setCreateTime(new Date());
        contest.setVote(0L);
        cultureContestMapper.insert(contest);
        return contest;
    }

    @Override
    public QinheCultureContest getContestByUserId(String userId, int type) {
        Condition condition = new Condition(QinheCultureContest.class);
        condition.createCriteria().andEqualTo("userId", userId).andEqualTo("type", type);
        List<QinheCultureContest> dataList = cultureContestMapper.selectByExample(condition);
        if (dataList != null && dataList.size() > 0) {
            return dataList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public QinheCultureContest getContestByUserId(String userId) {
        Condition condition = new Condition(QinheCultureContest.class);
        condition.createCriteria().andEqualTo("userId", userId);
        List<QinheCultureContest> dataList = cultureContestMapper.selectByExample(condition);
        if (dataList != null && dataList.size() > 0) {
            return dataList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public QinheCultureContest getContestById(Long id) {
        return cultureContestMapper.selectByPrimaryKey(id);
    }

    @Override
    public QinheCultureFile saveWork(String userId, int type, MultipartFile file) throws IOException {
        QinheCultureContest contest = getContestByUserId(userId, type);
        if (contest == null) {
            throw new IllegalArgumentException("你还没有注册");
        }

        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        String filePath = basePath + "/" + fileName;
        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(filePath));

        QinheCultureFile workFile = new QinheCultureFile();
        workFile.setContestId(contest.getId());
        workFile.setFile(fileName);
        workFile.setCreateTime(new Date());
        cultureFileMapper.insert(workFile);
        return workFile;
    }

    @Override
    public QinheCultureFile saveWork(Long id, MultipartFile file) throws IOException {
        QinheCultureContest contest = getContestById(id);
        if (contest == null) {
            throw new IllegalArgumentException("你还没有注册");
        }

        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        String filePath = basePath + "/" + fileName;
        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(filePath));

        QinheCultureFile workFile = new QinheCultureFile();
        workFile.setContestId(contest.getId());
        workFile.setFile(fileName);
        workFile.setCreateTime(new Date());
        cultureFileMapper.insert(workFile);
        return workFile;
    }

    @Override
    public List<QinheCultureFile> getContestWorkFiles(Long contestId) {
        Condition condition = new Condition(QinheCultureFile.class);
        condition.createCriteria().andEqualTo("contestId", contestId);
        return cultureFileMapper.selectByExample(condition);
    }

    @Override
    public List<QinheCultureContest> listAllWorks() {
        Condition condition = new Condition(QinheCultureContest.class);
        condition.setOrderByClause("vote desc");
        return cultureContestMapper.selectAll();
    }

    @Override
    public void vote(Long id) {
        cultureContestMapper.incrementVote(id);
    }
}
