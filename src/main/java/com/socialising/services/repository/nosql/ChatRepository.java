package com.socialising.services.repository.nosql;

import com.socialising.services.model.nosql.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findByType(String type);

    List<Chat> findByParticipantsContaining(String userId);

    List<Chat> findByRoomName(String roomName);

}
