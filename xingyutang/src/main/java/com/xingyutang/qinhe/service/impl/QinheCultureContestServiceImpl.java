package com.xingyutang.qinhe.service.impl;

import com.xingyutang.qinhe.mapper.QinheCultureContestMapper;
import com.xingyutang.qinhe.mapper.QinheCultureFileMapper;
import com.xingyutang.qinhe.mapper.QinheCultureVoteMapper;
import com.xingyutang.qinhe.model.entity.QinheCultureContest;
import com.xingyutang.qinhe.model.entity.QinheCultureFile;
import com.xingyutang.qinhe.model.entity.QinheCultureVote;
import com.xingyutang.qinhe.model.vo.RankingVO;
import com.xingyutang.qinhe.service.QinheCultureContestService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class QinheCultureContestServiceImpl implements QinheCultureContestService {
    private final static Logger logger = LoggerFactory.getLogger(QinheCultureContestServiceImpl.class);

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
    public List<QinheCultureContest> getContestsByUserId(String userId) {
        Condition condition = new Condition(QinheCultureContest.class);
        condition.createCriteria().andEqualTo("userId", userId);
        return cultureContestMapper.selectByExample(condition);
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
        File _file = new File(filePath);
        FileUtils.copyInputStreamToFile(file.getInputStream(), _file);

        String contentType = file.getContentType();

        if (contentType != null && contentType.matches("^image/.*$")) {
            generateThumb(_file, fileName);
            compressImageFile(_file);
        }


        QinheCultureFile workFile = new QinheCultureFile();
        workFile.setContestId(id);
        workFile.setFile(fileName);
        workFile.setContentType(file.getContentType());
        workFile.setCreateTime(new Date());
        cultureFileMapper.insert(workFile);
        return workFile;
    }

    private void generateThumb(File file, String name) {
        try {
            String thumbFilePath = basePath + "/thumb";
            new File(thumbFilePath).mkdirs();

            Thumbnails.of(file)
                    .size(300, 300)
                    .outputQuality(0.5f)
                    .toFile(thumbFilePath + "/" + name);

        } catch (Exception e) {
            logger.error("generate thumb error for file {}", name, e);
        }
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
    public File getThumbFile(QinheCultureFile cultureFile) {
        File file = new File(basePath + "/thumb/" + cultureFile.getFile());
        if (!file.exists()) {
            return getFile(cultureFile);
        }
        return file;
    }

    @Override
    public List<QinheCultureFile> getContestWorkFiles(Long contestId) {
        Condition condition = new Condition(QinheCultureFile.class);
        condition.createCriteria().andEqualTo("contestId", contestId);
        return cultureFileMapper.selectByExample(condition);
    }

    @Override
    public List<QinheCultureContest> listAllWorks() {
        return cultureContestMapper.selectAllWorks();
    }

    @Override
    public List<QinheCultureContest> listWorksByType(int type, int playerType) {
        List<QinheCultureContest> dataList = cultureContestMapper.selectWorksByType(type, playerType);
        loadWorkFiles(dataList);
        return dataList;
    }

    @Override
    public List<RankingVO> listRankingByType(int type, int playerType, int top) {
        return cultureContestMapper.selectRankingByType(type, playerType, top);
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

    @Override
    public InputStream exportAll() throws IOException {
        List<QinheCultureContest> dataList = listAllWorks();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            XSSFRow titleRow = sheet.createRow(rowIndex++);

            titleRow.createCell(colIndex++).setCellValue("序号");
            titleRow.createCell(colIndex++).setCellValue("参赛活动");
            titleRow.createCell(colIndex++).setCellValue("姓名");
            titleRow.createCell(colIndex++).setCellValue("性别");
            titleRow.createCell(colIndex++).setCellValue("年龄");
            titleRow.createCell(colIndex++).setCellValue("城市");
            titleRow.createCell(colIndex++).setCellValue("参赛通道");
            titleRow.createCell(colIndex++).setCellValue("所在小区/工作单位/公司名称");
            titleRow.createCell(colIndex++).setCellValue("联系电话");
            titleRow.createCell(colIndex++).setCellValue("作品名称");
            titleRow.createCell(colIndex++).setCellValue("得票");
            titleRow.createCell(colIndex++).setCellValue("创建时间");

            XSSFRow row;
            for (int i = 0; i < dataList.size(); i++) {
                row = sheet.createRow(rowIndex++);
                QinheCultureContest item = dataList.get(i);
                int j = 0;
                row.createCell(j++).setCellValue(String.valueOf(i + 1));
                row.createCell(j++).setCellValue(getContestNameByType(item.getType()));
                row.createCell(j++).setCellValue(item.getName());
                row.createCell(j++).setCellValue(item.getGender());
                row.createCell(j++).setCellValue(item.getAge());
                row.createCell(j++).setCellValue(item.getCity());
                row.createCell(j++).setCellValue(getContestChannel(item.getPlayerType()));
                row.createCell(j++).setCellValue(item.getAddress());
                row.createCell(j++).setCellValue(item.getPhoneNumber());
                row.createCell(j++).setCellValue(item.getWorkName());
                row.createCell(j++).setCellValue(item.getVote());
                row.createCell(j++).setCellValue(DateFormatUtils.format(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            return exportAsInputStream(wb);
        }
    }

    private String getContestNameByType(Integer type) {
        if (type == null) {
            return null;
        }
        if (type == 1) {
            return "美术书法大赛";
        }
        if (type == 2) {
            return "小小演说家";
        }
        if (type == 3) {
            return "诗词朗诵";
        }
        return null;
    }

    private String getContestChannel(Integer playerType) {
        if (playerType == null) {
            return null;
        }
        if (playerType == 1) {
            return "业主";
        }
        if (playerType == 2) {
            return "媒体";
        }
        if (playerType == 3) {
            return "员工";
        }
        return null;
    }

    private InputStream exportAsInputStream(Workbook wb) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            wb.write(out);
            InputStream in = new ByteArrayInputStream(out.toByteArray());
            return in;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeQuietly(out);
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore){}
        }
    }

    @Override
    public List<String> generateAllThumbs() {
        List<String> fileList = new ArrayList<>();
        File dir = new File(basePath);
        File[] files = dir.listFiles((file, s) -> {
            String name = s.toLowerCase();
            return name.matches("^.*\\.((jpg)|(jpeg)|(png))$");
        });

        if (files != null) {
            for (File f : files) {
                logger.info("generating thumb for {}", f.getName());
                generateThumb(f, f.getName());
                fileList.add(f.getName());
            }
        }

        return fileList;
    }

    @Override
    public List<String> compressAllImages() {
        List<String> fileList = new ArrayList<>();
        File dir = new File(basePath);
        File[] files = dir.listFiles((file, s) -> {
            String name = s.toLowerCase();
            return name.matches("^.*\\.((jpg)|(jpeg)|(png))$");
        });

        if (files != null) {
            for (File f : files) {
                logger.info("compress image {}", f.getName());
                compressImageFile(f);
                fileList.add(f.getName());
            }
        }
        return fileList;
    }

    @Override
    public List<QinheCultureContest> searchWork(Integer type, Integer playerType, String name, String phoneNumber) {
        Condition condition = new Condition(QinheCultureContest.class);
        Example.Criteria criteria = condition.createCriteria();
        if (type != null) {
            criteria.andEqualTo("type", type);
        }
        if (playerType != null) {
            criteria.andEqualTo("playerType", playerType);
        }
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", "%" + name + "%");
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            criteria.andEqualTo("phoneNumber", phoneNumber);
        }
        return cultureContestMapper.selectByExample(condition);
    }

    @Override
    public void loadWorkFiles(List<QinheCultureContest> contestList) {
        if (contestList != null) {
            for (QinheCultureContest contest : contestList) {
                contest.setFiles(getContestWorkFiles(contest.getId()));
            }
        }
    }

    private void compressImageFile(File imageFile) {
        try {
            Thumbnails.of(imageFile)
                    .scale(1.0)
                    .outputQuality(0.5f)
                    .toFile(imageFile);
        } catch (Exception e) {
            logger.error("compress image error: {}", imageFile.getName(), e);
        }
    }
}
