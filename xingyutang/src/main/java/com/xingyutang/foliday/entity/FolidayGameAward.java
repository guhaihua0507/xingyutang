package com.xingyutang.foliday.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "t_foliday_game_award")
public class FolidayGameAward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userGameId;
    private Integer card1;
    private Integer card2;
    private Integer card3;
    private Integer card4;
    private Integer awardType;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserGameId() {
        return userGameId;
    }

    public void setUserGameId(Long userGameId) {
        this.userGameId = userGameId;
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

    public Integer getAwardType() {
        return awardType;
    }

    public void setAwardType(Integer awardType) {
        this.awardType = awardType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
