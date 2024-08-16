package com.socialising.services.repository.nosql;

import com.socialising.services.model.nosql.ChatMsg;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMsgRepository extends MongoRepository<ChatMsg, String> {
    List<ChatMsg> findByRoomId(String roomId);
}
