package com.socialising.services.repository;

import com.socialising.services.constants.Status;
import com.socialising.services.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM socialise.user WHERE phonenumber=?", nativeQuery = true)
    Optional<User> findByPhoneNumber(String phoneNo);

    @Query(value = "SELECT * FROM socialise.user WHERE email=?", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM socialise.user WHERE username=?", nativeQuery = true)
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM socialise.user WHERE username LIKE %?1%", nativeQuery = true)
    List<User> searchUserByWord(String word);

    List<User> findAllByStatus(Status status);
}
