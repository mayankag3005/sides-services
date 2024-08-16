package com.socialising.services.repository.nosql;

import com.socialising.services.model.nosql.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {
    List<Video> findByAssociatedMessageId(String messageId);
}
