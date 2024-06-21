package com.socialising.services.repository;

import com.socialising.services.model.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
            select t from Token t inner join User u on t.user.userId = u.userId
            where u.userId = :userId and (t.expired = false or t.revoked = false)
            """)
    List<Token> findAllValidTokens(Long userId);

    Optional<Token> findByToken(String token);
}
