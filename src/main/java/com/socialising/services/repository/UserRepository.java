package com.socialising.services.repository;

import com.socialising.services.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM socialise.user WHERE phonenumber=?", nativeQuery = true)
    User findByPhoneNumber(String phoneNo);

    @Query(value = "SELECT * FROM socialise.user WHERE username LIKE %?1%", nativeQuery = true)
    List<User> searchUserByWord(String word);

//    @Query(value = "SELECT * FROM socialise.user WHERE %?1% IN tags", nativeQuery = true)
//    List<User> searchUserByTag(String tag);
}
