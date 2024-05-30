package com.socialising.services.repository;

import com.socialising.services.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM socialise.user WHERE phonenumber=?", nativeQuery = true)
    User findByPhoneNumber(String phoneNo);
}
