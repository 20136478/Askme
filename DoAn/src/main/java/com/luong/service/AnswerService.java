package com.luong.service;

import com.luong.model.Answer;
import com.luong.model.DTO.QuestionDTO;
import com.luong.model.Question;

import java.util.List;
import java.util.Map;

/**
 * Created by Luong-PC on 4/3/2017.
 */
public interface AnswerService {
    Map<QuestionDTO, Long> count();
    List<Answer> la();
    public void add(Answer answer,int id);
    public List<Answer> listAnswerOfQuestion(int id);

}
