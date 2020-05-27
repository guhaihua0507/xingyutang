package com.xingyutang.foliday.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Table(name = "t_foliday_game")
public class FolidayGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String name;
    private String phoneNumber;
    private Integer stage;
    private Integer coin;
    private Integer card1;
    private Integer card2;
    private Integer card3;
    private Integer card4;
    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
    }

    public Integer getCard1() {
        return card1;
    }

    public void setCard1(Integer card1) {
        this.card1 = card1;
    }

    public Integer getCard2() {
        return card2;
    }

    public void setCard2(Integer card2) {
        this.card2 = card2;
    }

    public Integer getCard3() {
        return card3;
    }

    public void setCard3(Integer card3) {
        this.card3 = card3;
    }

    public Integer getCard4() {
        return card4;
    }

    public void setCard4(Integer card4) {
        this.card4 = card4;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
