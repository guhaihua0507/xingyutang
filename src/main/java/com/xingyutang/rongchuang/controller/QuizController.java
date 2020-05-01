package com.xingyutang.rongchuang.controller;

import com.xingyutang.app.model.vo.ResponseData;
import com.xingyutang.lvcheng.mapper.QuizAnswerMapper;
import com.xingyutang.rongchuang.model.entity.QuizAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/rongchuang/quiz")
public class QuizController {

    @Autowired
    private QuizAnswerMapper quizAnswerMapper;

    @PostMapping("/answer")
    public ResponseData createQuizAnswer(@RequestBody QuizAnswer quizAnswer) {
        if (quizAnswerMapper.existsWithPrimaryKey(quizAnswer.getUserId())) {
            quizAnswerMapper.updateByPrimaryKey(quizAnswer);
        } else {
            quizAnswerMapper.insert(quizAnswer);
        }
        return ResponseData.ok(quizAnswer);
    }

    @GetMapping("/answer/list")
    public ResponseData getQuizAnswerList() {
        return ResponseData.ok(quizAnswerMapper.selectAll());
    }
}
