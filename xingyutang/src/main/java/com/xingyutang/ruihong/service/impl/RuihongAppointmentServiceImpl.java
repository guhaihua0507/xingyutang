package com.xingyutang.ruihong.service.impl;

import com.xingyutang.app.service.AppGenericService;
import com.xingyutang.ruihong.entity.RuihongAppointment;
import com.xingyutang.ruihong.mapper.RuihongAppointmentMapper;
import com.xingyutang.ruihong.service.RuihongAppointmentService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class RuihongAppointmentServiceImpl implements RuihongAppointmentService {
    @Autowired
    private RuihongAppointmentMapper appointmentMapper;
    @Autowired
    private AppGenericService appGenericService;

    @Override
    public RuihongAppointment save(RuihongAppointment appointment) {
        RuihongAppointment entity = getByUserId(appointment.getUserId());
        if (entity == null) {
            appointment.setCreateTime(new Date());
            appointment.setId(null);
            appointmentMapper.insert(appointment);
        } else {
            if (appointment.getKawsNumber() > entity.getKawsNumber()) {
                appointment.setId(entity.getId());
                appointment.setCreateTime(new Date());
                appointmentMapper.updateByPrimaryKey(appointment);
            }
        }
        return appointment;
    }


    @Override
    public List<RuihongAppointment> listRanking() {
        Condition condition = new Condition(RuihongAppointment.class);
        condition.setOrderByClause("kaws_number desc, create_time");

        List<RuihongAppointment> dataList = appointmentMapper.selectByExample(condition);
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                RuihongAppointment appointment = dataList.get(i);
                appointment.setRanking(i + 1);
            }
        }
        return dataList;
    }

    @Override
    public RuihongAppointment getByUserId(String userId) {
        Condition condition = new Condition(RuihongAppointment.class);
        condition.createCriteria().andEqualTo("userId", userId);
        List<RuihongAppointment> dataList = appointmentMapper.selectByExample(condition);
        if (dataList != null && dataList.size() > 0) {
            return dataList.get(0);
        }
        return null;
    }

    @Override
    public List<RuihongAppointment> listAll() {
        return appointmentMapper.selectAll();
    }

    @Override
    public InputStream exportAll() throws IOException {
        List<RuihongAppointment> dataList = listAll();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            XSSFRow titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(colIndex++).setCellValue("序号");
            titleRow.createCell(colIndex++).setCellValue("昵称");
            titleRow.createCell(colIndex++).setCellValue("手机");
            titleRow.createCell(colIndex++).setCellValue("KAWS");
            titleRow.createCell(colIndex++).setCellValue("创建时间");

            XSSFRow row;
            for (int i = 0; i < dataList.size(); i++) {
                row = sheet.createRow(rowIndex++);
                RuihongAppointment item = dataList.get(i);
                int j = 0;
                row.createCell(j++).setCellValue(String.valueOf(i + 1));
                row.createCell(j++).setCellValue(item.getNickName());
                row.createCell(j++).setCellValue(item.getPhoneNumber());
                row.createCell(j++).setCellValue(item.getKawsNumber());
                row.createCell(j++).setCellValue(DateFormatUtils.format(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            }

            return appGenericService.exportAsInputStream(wb);
        }
    }
}
