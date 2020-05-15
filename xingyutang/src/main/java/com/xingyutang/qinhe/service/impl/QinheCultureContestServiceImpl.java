package com.xingyutang.qinhe.service.impl;

import com.xingyutang.qinhe.mapper.QinheCultureContestMapper;
import com.xingyutang.qinhe.mapper.QinheCultureFileMapper;
import com.xingyutang.qinhe.mapper.QinheCultureVoteMapper;
import com.xingyutang.qinhe.model.entity.QinheCultureContest;
import com.xingyutang.qinhe.model.entity.QinheCultureFile;
import com.xingyutang.qinhe.model.entity.QinheCultureVote;
import com.xingyutang.qinhe.service.QinheCultureContestService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class QinheCultureContestServiceImpl implements QinheCultureContestService {
    @Autowired
    private QinheCultureContestMapper cultureContestMapper;
    @Autowired
    private QinheCultureFileMapper cultureFileMapper;
    @Autowired
    private QinheCultureVoteMapper cultureVoteMapper;

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
    @Transactional
    public QinheCultureContest updateSignInfo(QinheCultureContest contest) {
        contest.setCreateTime(new Date());
        cultureContestMapper.updateByPrimaryKey(contest);
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
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        String filePath = basePath + "/" + fileName;
        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(filePath));

        QinheCultureFile workFile = new QinheCultureFile();
        workFile.setContestId(id);
        workFile.setFile(fileName);
        workFile.setContentType(file.getContentType());
        workFile.setCreateTime(new Date());
        cultureFileMapper.insert(workFile);
        return workFile;
    }

    @Override
    @Transactional
    public void updateWorkFiles(Long id, MultipartFile[] files) throws IOException {
        QinheCultureContest contest = getContestById(id);
        if (contest == null) {
            throw new IllegalArgumentException("你还没有注册");
        }

        Condition condition = new Condition(QinheCultureFile.class);
        condition.createCriteria().andEqualTo("contestId", id);
        cultureFileMapper.deleteByExample(condition);

        for (MultipartFile file : files) {
            saveWork(id, file);
        }
    }

    @Override
    public QinheCultureFile getCultureFileById(Long id) {
        return cultureFileMapper.selectByPrimaryKey(id);
    }

    @Override
    public File getFile(QinheCultureFile cultureFile) {
        return new File(basePath + "/" + cultureFile.getFile());
    }

    @Override
    public List<QinheCultureFile> getContestWorkFiles(Long contestId) {
        Condition condition = new Condition(QinheCultureFile.class);
        condition.createCriteria().andEqualTo("contestId", contestId);
        return cultureFileMapper.selectByExample(condition);
    }

    @Override
    public List<QinheCultureContest> listAllWorks() {
        return cultureContestMapper.selectAll();
    }

    @Override
    public List<QinheCultureContest> listRankingByType(int type) {
        Condition condition = new Condition(QinheCultureContest.class);
        condition.createCriteria().andEqualTo("type", type);
        condition.setOrderByClause("vote desc");
        List<QinheCultureContest> dataList = cultureContestMapper.selectByExample(condition);
        if (dataList != null) {
            for (QinheCultureContest contest : dataList) {
                contest.setFiles(getContestWorkFiles(contest.getId()));
            }
        }
        return dataList;
    }

    @Override
    @Transactional
    public void vote(Long id, String userId, Integer type) {
        QinheCultureVote vote = new QinheCultureVote();
        vote.setUserId(userId);
        vote.setContestId(id);
        vote.setType(type);
        vote.setCreateTime(new Date());

        cultureVoteMapper.insert(vote);
        cultureContestMapper.incrementVote(id);
    }

    @Override
    public int validateVote(Long id, Integer type, String userId) {
        Condition condition = new Condition(QinheCultureVote.class);
        condition.createCriteria().andEqualTo("contestId", id).andEqualTo("userId", userId);
        int count = cultureVoteMapper.selectCountByExample(condition);
        if (count > 0) {
            return 1;   //重复投票
        }

        Date startTime = DateUtils.truncate(new Date(), Calendar.DATE);
        Date endTime = DateUtils.addDays(startTime, 1);
        Condition condition2 = new Condition(QinheCultureVote.class);
        condition2.createCriteria().andEqualTo("userId", userId)
                .andEqualTo("type", type)
                .andBetween("createTime", startTime, endTime);
        int count2 = cultureVoteMapper.selectCountByExample(condition2);
        if (count2 >= 1) {
            return 2;   //达到投票上限
        }
        return 0;
    }
}
