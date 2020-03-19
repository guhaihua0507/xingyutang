package com.xingyutang.service.impl;

import com.xingyutang.exception.RequestException;
import com.xingyutang.mapper.RongchuangSupplierMapper;
import com.xingyutang.model.entity.RongchuangSupplier;
import com.xingyutang.service.RongchuangSupplierConventionService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RongchuangSupplierConventionServiceImpl implements RongchuangSupplierConventionService {
    private Logger logger		= LoggerFactory.getLogger(RongchuangSupplierConventionServiceImpl.class);

    private final static String SIGN_IN_TIME = "2020-03-20 19:20:00";
    private final static String SIGN_IN_END_TIME = "2020-03-20 19:30:00";
    private final static String LOTTERY_TIME =  "2020-03-20 21:50:00";
    private Date signTime;
    private Date signEndTime;
    private Date lotteryTime;
    private List<Long> users = new CopyOnWriteArrayList<>();
    private List<Integer> prizes = new CopyOnWriteArrayList<>();
    private Set<Long> passedUsers = new HashSet<>();

    @Autowired
    private RongchuangSupplierMapper rongchuangSupplierMapper;

    public RongchuangSupplierConventionServiceImpl() throws ParseException {
        signTime = DateUtils.parseDate(SIGN_IN_TIME, "yyyy-MM-dd HH:mm:ss");
        signEndTime = DateUtils.parseDate(SIGN_IN_END_TIME, "yyyy-MM-dd HH:mm:ss");
        lotteryTime = DateUtils.parseDate(LOTTERY_TIME, "yyyy-MM-dd HH:mm:ss");

        /*
         * init prize
        */
        for (int i = 0; i < 201; i++) {
            prizes.add(i);
        }

        logger.info("prize inited {}", prizes);
    }

    @Scheduled(cron = "0 30 21 20 3 ?")
    public void initLottery() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        if (year != 2020) {
            return;
        }

        logger.info("init lottery users");

        List<RongchuangSupplier> suppliers = rongchuangSupplierMapper.selectAll();
        synchronized (RongchuangSupplierConventionService.class) {
            suppliers.forEach(e -> {
                if (e.getSignTime() != null) {
                    users.add(e.getUserId());
                }
            });
        }
        logger.info("all supplier loaded {} users", users.size());
    }

    @Transactional
    @Override
    public RongchuangSupplier createSupplier(RongchuangSupplier entity) {
        rongchuangSupplierMapper.insert(entity);
        return entity;
    }

    @Override
    public List<RongchuangSupplier> listSuppliers() {
        return rongchuangSupplierMapper.selectAll();
    }

    @Override
    public RongchuangSupplier getSupplierByUserId(Long userId) {
        Condition condition = new Condition(RongchuangSupplier.class);
        condition.createCriteria().andEqualTo("userId", userId);
        List<RongchuangSupplier> results = rongchuangSupplierMapper.selectByExample(condition);
        return results != null && results.size() > 0 ? results.get(0) : null;
    }

    @Transactional
    @Override
    public void signIn(Long userId) throws RequestException {
        if (System.currentTimeMillis() < signTime.getTime()) {
            throw new RequestException(1, "会议尚未开始，请耐心等待");
        }
        if (System.currentTimeMillis() > signEndTime.getTime()) {
            throw new RequestException(1, "签到已经结束");
        }
        RongchuangSupplier supplier = getSupplierByUserId(userId);
        if (supplier == null) {
            throw new RequestException(2, "你还没有预约");
        }
        supplier.setSignTime(new Date());
        rongchuangSupplierMapper.updateByPrimaryKey(supplier);
    }

    @Transactional
    @Override
    public int lottery(long userId) throws RequestException {
        if (System.currentTimeMillis() < lotteryTime.getTime()) {
            throw new RequestException(1, "抽奖未开始");
        }

        RongchuangSupplier rongchuangSupplier = getSupplierByUserId(userId);
        if (rongchuangSupplier == null) {
            throw new RequestException(2, "你还没有预约");
        }

        if (rongchuangSupplier.getPrize() != null) {
            return rongchuangSupplier.getPrize();
        }

        int prize = -1;
        synchronized (RongchuangSupplierConventionService.class) {
            if (users.indexOf(userId) == -1) {
                users.add(userId);
            }

            if (prizes.size() == 0) {
                throw new RequestException(3, "你来晚了，奖品已经抽完");
            }

            Random random = new Random();
            int idx = random.nextInt(Math.max(users.size() - passedUsers.size(), prizes.size()));

            if (idx < prizes.size()) {
                prize = prizes.get(idx);
                prizes.remove(idx);
            } else {
                prize = -1;
            }
            passedUsers.add(userId);
        }

        rongchuangSupplier.setPrize(prize);
        rongchuangSupplierMapper.updateByPrimaryKey(rongchuangSupplier);

        return prize;
    }

    @Override
    public InputStream exportSuppliers() throws IOException {
        List<RongchuangSupplier> suppliers = listSuppliers();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            XSSFRow titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(colIndex++).setCellValue("序号");
            titleRow.createCell(colIndex++).setCellValue("供应商");
            titleRow.createCell(colIndex++).setCellValue("姓名");
            titleRow.createCell(colIndex++).setCellValue("手机号");

            XSSFRow row;
            XSSFCell cell;
            for (int i = 0; i < suppliers.size(); i++) {
                row = sheet.createRow(rowIndex++);
                RongchuangSupplier supplier = suppliers.get(i);
                int j = 0;
                row.createCell(j++).setCellValue(String.valueOf(i + 1));
                row.createCell(j++).setCellValue(supplier.getSupplierName());
                row.createCell(j++).setCellValue(supplier.getName());
                row.createCell(j++).setCellValue(supplier.getPhoneNumber());
            }
            return exportAsInputStream(wb);
        }
    }

    private InputStream exportAsInputStream(Workbook wb) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            wb.write(out);
//            wb.close();
            InputStream in = new ByteArrayInputStream(out.toByteArray());
            out.close();
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
