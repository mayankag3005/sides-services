package com.socialising.services.repository;

import com.socialising.services.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository<PostDao, Long> -> PostDao : type of model/data and Long : type of id
// JpaRepository provides all the predefined methods to access the model such as findAll, findById

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOwnerUserUsername(String username);
}
