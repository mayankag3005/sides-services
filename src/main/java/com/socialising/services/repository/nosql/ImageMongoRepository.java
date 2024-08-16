package com.socialising.services.repository.nosql;

import com.socialising.services.model.nosql.ImageMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageMongoRepository extends MongoRepository<ImageMongo, String> {
    Optional<ImageMongo> findByAssociatedUsername(String username);
    Optional<ImageMongo> findByAssociatedRoomId(String roomId);
    List<ImageMongo> findByAssociatedMessageId(String messageId);
}
