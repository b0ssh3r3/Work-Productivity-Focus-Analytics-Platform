package com.focusanalytics.insightservice.respository;

import com.focusanalytics.insightservice.model.Insight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsightRepository extends MongoRepository<Insight, String> {
    List<Insight> findByUserId(String userId);

    Optional<Insight> findByFocusSessionId(String focusSessionId);
}


