package com.xingyutang.dahua.service.impl;

import com.xingyutang.app.service.AppGenericService;
import com.xingyutang.dahua.entity.SpringCityAward;
import com.xingyutang.dahua.entity.SpringCityPowerSupport;
import com.xingyutang.dahua.entity.SpringCityUser;
import com.xingyutang.dahua.entity.SpringCityUserAward;
import com.xingyutang.dahua.mapper.SpringCityAwardMapper;
import com.xingyutang.dahua.mapper.SpringCityPowerSupportMapper;
import com.xingyutang.dahua.mapper.SpringCityUserAwardMapper;
import com.xingyutang.dahua.mapper.SpringCityUserMapper;
import com.xingyutang.dahua.service.SpringCityService;
import com.xingyutang.foliday.entity.FolidayGameAward;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpringCityServiceImpl implements SpringCityService {
    private final static int MAX_STAGE = 3;

    @Autowired
    private SpringCityUserMapper userMapper;
    @Autowired
    private SpringCityPowerSupportMapper powerSupportMapper;
    @Autowired
    private SpringCityAwardMapper awardMapper;
    @Autowired
    private SpringCityUserAwardMapper userAwardMapper;
    @Autowired
    private AppGenericService appGenericService;

    @Override
    public SpringCityUser signIn(SpringCityUser user) {
        user.setPower(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insert(user);
        return user;
    }

    @Override
    public SpringCityUser getUserByWxOpenId(String wxOpenId) {
        Condition condition = new Condition(SpringCityUser.class);
        condition.and().andEqualTo("wxOpenId", wxOpenId);
        return userMapper.selectOneByExample(condition);
    }

    @Override
    public SpringCityUser getUserById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public SpringCityUser updateUser(SpringCityUser userVo) {
        SpringCityUser entity = getUserById(userVo.getId());
        if (entity == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        entity.setName(userVo.getName());
        entity.setPhoneNumber(userVo.getPhoneNumber());
        entity.setUpdateTime(new Date());
        userMapper.updateByPrimaryKey(entity);
        return entity;
    }

    private SpringCityPowerSupport getPowerSupport(Long userId, String wxOpenId) {
        Condition condition = new Condition(SpringCityPowerSupport.class);
        condition.and().andEqualTo("userId", userId).andEqualTo("wxOpenId", wxOpenId);
        List<SpringCityPowerSupport> datalist = powerSupportMapper.selectByExample(condition);
        if (datalist != null && datalist.size() > 0) {
            return datalist.get(0);
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void addPowerByWxUser(Long userId, String wxOpenId) {
        SpringCityUser user = getUserById(userId);
        if (user == null || user.getWxOpenId().equals(wxOpenId)) {
            return;
        }
        SpringCityPowerSupport powerSupport = getPowerSupport(userId, wxOpenId);
        if (powerSupport != null) {
            return;
        }
        powerSupport = new SpringCityPowerSupport();
        powerSupport.setUserId(userId);
        powerSupport.setWxOpenId(wxOpenId);
        powerSupport.setCreateTime(new Date());

        powerSupportMapper.insert(powerSupport);

        userMapper.addPowerByUserId(userId);
    }

    @Override
    public List<SpringCityAward> getAwards() {
        return awardMapper.selectAll();
    }

    @Override
    @Transactional
    public synchronized void claimAward(Long userId, Integer awardType) {
        SpringCityUserAward userAward = getUserAward(userId);

        if (userAward != null) {
            throw new IllegalStateException("你已经兑换过奖品");
        }
        SpringCityUser user = getUserById(userId);

        SpringCityAward award = getAward(awardType);
        if (award == null) {
            throw new IllegalStateException("不存在的奖品类型:" + awardType);
        }
        if (user.getPower() < award.getRequirePower()) {
            throw new IllegalStateException("你的助力值不够");
        }

        if (award.getAmount() <= 0) {
            throw new IllegalStateException("该奖品已经兑完");
        }

        user.setPower(user.getPower() - award.getRequirePower());
        user.setUpdateTime(new Date());
        userMapper.updateByPrimaryKey(user);

        award.setAmount(award.getAmount() - 1);
        awardMapper.updateByPrimaryKey(award);

        userAward = new SpringCityUserAward();
        userAward.setAwardType(awardType);
        userAward.setUserId(userId);
        userAward.setCreateTime(new Date());
        userAwardMapper.insert(userAward);
    }

    private Map<Integer, SpringCityAward> getAwardMap() {
        List<SpringCityAward> awards = getAwards();
        return awards.stream().collect(Collectors.toMap(SpringCityAward::getId, a -> a));
    }

    private SpringCityAward getAward(Integer awardType) {
        return awardMapper.selectByPrimaryKey(awardType);
    }

    private int getRequiredPowerForAward(int awardType) {
        switch (awardType) {
            case 1: return 8;
            case 2: return 18;
            case 3: return 38;
            case 4: return 108;
        }
        throw new IllegalArgumentException("不存在的奖品类型: " + awardType);
    }

    @Override
    public SpringCityUserAward getUserAward(Long userId) {
        Condition condition = new Condition(SpringCityUserAward.class);
        condition.and().andEqualTo("userId", userId);
        List<SpringCityUserAward> dataList = userAwardMapper.selectByExample(condition);
        if (CollectionUtils.isNotEmpty(dataList)) {
            return dataList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<SpringCityUser> listAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public InputStream exportAll() throws IOException {
        List<SpringCityUser> dataList = listAllUsers();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            XSSFRow titleRow = sheet.createRow(rowIndex++);

            titleRow.createCell(colIndex++).setCellValue("序号");
            titleRow.createCell(colIndex++).setCellValue("姓名");
            titleRow.createCell(colIndex++).setCellValue("电话");
            titleRow.createCell(colIndex++).setCellValue("创建时间");

            XSSFRow row;
            for (int i = 0; i < dataList.size(); i++) {
                row = sheet.createRow(rowIndex++);
                SpringCityUser item = dataList.get(i);
                int j = 0;
                row.createCell(j++).setCellValue(String.valueOf(i + 1));
                row.createCell(j++).setCellValue(item.getName());
                row.createCell(j++).setCellValue(item.getPhoneNumber());
                row.createCell(j++).setCellValue(DateFormatUtils.format(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            return appGenericService.exportAsInputStream(wb);
        }
    }
}
