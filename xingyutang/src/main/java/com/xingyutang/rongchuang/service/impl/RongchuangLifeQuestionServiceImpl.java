package com.xingyutang.rongchuang.service.impl;

import com.xingyutang.lvcheng.model.entity.LvchengPoetContest;
import com.xingyutang.rongchuang.mapper.RongchuangLifeQuestionMapper;
import com.xingyutang.rongchuang.model.entity.RongchuangLifeQuestion;
import com.xingyutang.rongchuang.model.entity.RongchuangSeasonPlay;
import com.xingyutang.rongchuang.model.vo.LifeQuestionResultVo;
import com.xingyutang.rongchuang.service.RongchuangLifeQuestionService;
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
import tk.mybatis.mapper.entity.Condition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class RongchuangLifeQuestionServiceImpl implements RongchuangLifeQuestionService {
    private Logger logger		= LoggerFactory.getLogger(RongchuangLifeQuestionServiceImpl.class);

    @Autowired
    private RongchuangLifeQuestionMapper lifeQuestionMapper;

    @Override
    public RongchuangLifeQuestion selectByUserId(Long userId) {
        Condition condition = new Condition(RongchuangSeasonPlay.class);
        condition.createCriteria().andEqualTo("userId", userId);
        condition.setOrderByClause("create_date desc");

        List<RongchuangLifeQuestion> dataList = lifeQuestionMapper.selectByExample(condition);
        if (CollectionUtils.isNotEmpty(dataList)) {
            return dataList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public RongchuangLifeQuestion saveQuestionResult(Long userId, RongchuangLifeQuestion question) {
        RongchuangLifeQuestion entity = selectByUserId(userId);
        if (entity != null) {
            entity.setResult(question.getResult());
            entity.setUpdateDate(new Date());
            lifeQuestionMapper.updateByPrimaryKey(entity);
        } else {
            entity = new RongchuangLifeQuestion();
            entity.setUserId(userId);
            entity.setResult(question.getResult());
            entity.setCreateDate(new Date());
            entity.setUpdateDate(new Date());
            lifeQuestionMapper.insert(entity);
        }

        return entity;
    }

    @Override
    public List<LifeQuestionResultVo> listAll() {
        return lifeQuestionMapper.listAllResult();
    }

    @Override
    public InputStream exportAll() throws IOException {
        List<LifeQuestionResultVo> dataList = listAll();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            XSSFRow titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(colIndex++).setCellValue("序号");
            titleRow.createCell(colIndex++).setCellValue("姓名");
            titleRow.createCell(colIndex++).setCellValue("结果");

            XSSFRow row;
            for (int i = 0; i < dataList.size(); i++) {
                row = sheet.createRow(rowIndex++);
                LifeQuestionResultVo item = dataList.get(i);
                int j = 0;
                row.createCell(j++).setCellValue(String.valueOf(i + 1));
                row.createCell(j++).setCellValue(item.getWxNickName());
                row.createCell(j++).setCellValue(item.getResult());
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
