package com.xingyutang.lvcheng.service.impl;

import com.xingyutang.lvcheng.mapper.LvchengPoetContestMapper;
import com.xingyutang.lvcheng.model.entity.LvchengPoetContest;
import com.xingyutang.lvcheng.service.LvchengPoetContestService;
import com.xingyutang.rongchuang.model.entity.RongchuangSupplier;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class LvchengPoetContestServiceImpl implements LvchengPoetContestService {
    private final static Logger logger = LoggerFactory.getLogger(LvchengPoetContestServiceImpl.class);
    @Autowired
    private LvchengPoetContestMapper lvchengPoetContestMapper;

    @Override
    @Transactional
    public LvchengPoetContest updateContestResult(LvchengPoetContest contest) {
        fillDefault(contest);
        contest.setCreateTime(new Date());
        LvchengPoetContest entity = getContestResultByUserId(contest.getUserId());

        if (entity == null) {
            lvchengPoetContestMapper.insert(contest);
            return contest;
        }

        fillDefault(entity);
        contest.setId(entity.getId());
        if (entity.getScore() > contest.getScore()
                || (entity.getScore() == contest.getScore() && entity.getUsedTime() <= contest.getUsedTime())) {
            return entity;
        } else {
            lvchengPoetContestMapper.updateByPrimaryKey(contest);
            return contest;
        }
    }

    private void fillDefault(LvchengPoetContest contest) {
        if (contest.getScore() == null) {
            contest.setScore(0);
        }
        if (contest.getUsedTime() == null) {
            contest.setUsedTime(0);
        }
    }

    @Override
    public LvchengPoetContest getContestResultByUserId(String userId) {
        Condition condition = new Condition(LvchengPoetContest.class);
        condition.createCriteria().andEqualTo("userId", userId);
        List<LvchengPoetContest> resultList =  lvchengPoetContestMapper.selectByExample(condition);
        return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
    }

    @Override
    public List<LvchengPoetContest> listRanking() {
        return lvchengPoetContestMapper.selectTop10Ranking();
    }

    @Override
    public List<LvchengPoetContest> listAll() {
        return lvchengPoetContestMapper.selectAll();
    }

    @Override
    public InputStream exportAll() throws IOException {
        List<LvchengPoetContest> suppliers = listAll();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            XSSFRow titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(colIndex++).setCellValue("序号");
            titleRow.createCell(colIndex++).setCellValue("姓名");
            titleRow.createCell(colIndex++).setCellValue("得分");
            titleRow.createCell(colIndex++).setCellValue("用时");

            XSSFRow row;
            XSSFCell cell;
            for (int i = 0; i < suppliers.size(); i++) {
                row = sheet.createRow(rowIndex++);
                LvchengPoetContest item = suppliers.get(i);
                int j = 0;
                row.createCell(j++).setCellValue(String.valueOf(i + 1));
                row.createCell(j++).setCellValue(item.getNickName());
                row.createCell(j++).setCellValue(item.getScore());
                row.createCell(j++).setCellValue(item.getUsedTime());
            }
            return exportAsInputStream(wb);
        }
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
}
