package com.socialising.services.repository.nosql;

import com.socialising.services.model.nosql.GroupInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupInfoRepository extends MongoRepository<GroupInfo, String> {
}
