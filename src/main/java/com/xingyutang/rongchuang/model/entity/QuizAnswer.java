package com.xingyutang.rongchuang.model.entity;

import javax.persistence.Id;

public class QuizAnswer {
    @Id
    private String userId;      //主键
    private String answer;

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
}
