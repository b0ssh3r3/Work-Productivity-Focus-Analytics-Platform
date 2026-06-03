package com.focusanalytics.focussessionservice;

import com.focusanalytics.focussessionservice.model.FocusSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FocusSessionRepository extends MongoRepository<FocusSession, String> {
    List<FocusSession> findByUserId(String userId);
}


