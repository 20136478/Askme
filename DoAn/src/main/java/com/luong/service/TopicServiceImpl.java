package com.luong.service;

import com.luong.dao.TopicDAO;
import com.luong.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    TopicDAO topicDAO;

    @Override
    public Topic find(String name) {
        return topicDAO.found(name);
    }

    @Override
    public List<Topic> listTopic() {
        return topicDAO.listTopic();
    }

    @Override
    public Topic findById(int id) {
        return topicDAO.findTopic(id);
    }
}
