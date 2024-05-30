package com.socialising.services.repository;

import com.socialising.services.model.Tag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query(value = "SELECT * FROM socialise.tag WHERE tag=?", nativeQuery = true)
    Tag findByTagName(String tagname);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM socialise.tag WHERE tag=?", nativeQuery = true)
    void deleteTagByName(String tagName);
}
