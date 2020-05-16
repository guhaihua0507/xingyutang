package com.xingyutang.qinhe.service;

import com.xingyutang.qinhe.model.entity.QinheCultureContest;
import com.xingyutang.qinhe.model.entity.QinheCultureFile;
import com.xingyutang.qinhe.model.vo.RankingVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface QinheCultureContestService {
    QinheCultureContest signUp(QinheCultureContest contest);
    
    QinheCultureContest updateSignInfo(QinheCultureContest contest);

    QinheCultureContest getContestByUserId(String userId, int type);

    QinheCultureContest getContestByUserId(String userId);

    QinheCultureContest getContestById(Long id);

    QinheCultureFile saveWork(String userId, int type, MultipartFile file) throws IOException;

    QinheCultureFile saveWork(Long id, MultipartFile file) throws IOException;

    File getFile(QinheCultureFile cultureFile);

    List<QinheCultureFile> getContestWorkFiles(Long contestId);

    List<QinheCultureContest> listAllWorks();

    void vote(Long id, String userId, Integer type);

    int validateVote(Long id, Integer type, String userId);

    List<QinheCultureContest> listWorksByType(int type);

    void updateWorkFiles(Long id, MultipartFile[] files) throws IOException;

    QinheCultureFile getCultureFileById(Long id);

    InputStream exportAll() throws IOException;

    File getThumbFile(QinheCultureFile cultureFile);

    List<RankingVO> listRankingByType(int type, int top);

    List<String> generateAllThumbs();
}
