package com.socialising.services.service;

import com.socialising.services.model.Tag;
import com.socialising.services.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TagService {

    private static final Logger log = LoggerFactory.getLogger(com.socialising.services.controller.TagController.class);

    @Autowired
    private final TagRepository tagRepository;

    // Add a tag to DB
    public Tag addTag(Tag tag) {
        tag.setTagId();
        try {
            this.tagRepository.save(tag);
            log.info("Tag added to db: {}", tag.getTagId());
            return tag;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    // Get All Tags
    public ArrayList<Tag> getAllTags() {
        log.info("Total number of tags: {}", this.tagRepository.count());

        return (ArrayList<Tag>) this.tagRepository.findAll();
    }

    // DELETE a Tag by Tag Id
    public int deleteTagById(Long tagId) {
        if(this.tagRepository.findById(tagId).isEmpty()) {
            log.info("No such tag {} exists in DB", tagId);
            return -1;
        }
        this.tagRepository.deleteById(tagId);
        log.info("Tag {} deleted from DB", tagId);
        return 1;
    }

    // DELETE Tag by tagName
    public void deleteTagByName(String tagName) {
        if (this.tagRepository.findByTagName(tagName) != null) {
            log.info("Tag with name {} exists in DB", tagName);
            this.tagRepository.deleteTagByName(tagName);
        }
        else {
            log.info("No Tag exists with Name: {}", tagName);
        }
    }

}
