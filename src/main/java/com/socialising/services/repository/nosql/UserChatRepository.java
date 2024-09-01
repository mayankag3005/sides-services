package com.socialising.services.repository.nosql;

import com.socialising.services.model.nosql.UserChat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserChatRepository extends MongoRepository<UserChat, String> {
    Optional<UserChat> findByUsername(String username);
}
