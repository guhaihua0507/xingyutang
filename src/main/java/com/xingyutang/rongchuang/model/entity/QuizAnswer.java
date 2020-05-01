package com.xingyutang.rongchuang.model.entity;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "t_quiz_answer")
public class QuizAnswer {
    @Id
    private String userId;      //主键
    private String answer;
    private Date createTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
