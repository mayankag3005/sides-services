package com.socialising.services.repository;

import com.socialising.services.model.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<PostDao, Long> -> ImageDao : type of model/data and Long : type of id
// JpaRepository provides all the predefined methods to access the model such as findAll, findById

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}
