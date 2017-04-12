package com.luong.service;

import com.luong.dao.TopicDAO;
import com.luong.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    TopicDAO topicDAO;

    @Override
    public Topic find(String name) {
        return topicDAO.found(name);
    }
}