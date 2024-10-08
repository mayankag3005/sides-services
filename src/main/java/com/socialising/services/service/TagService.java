package com.socialising.services.service;

import com.socialising.services.model.Tag;
import com.socialising.services.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TagService {

    private static final Logger log = LoggerFactory.getLogger(com.socialising.services.controller.TagController.class);

    @Autowired
    private final TagRepository tagRepository;

    // Add a tag to DB
    public Tag addTag(Tag tag) {

        // Convert the tag to lowercase
        String lowerCaseTag = tag.getTag().toLowerCase();
        // Update tag object with the lowercase version
        tag.setTag(lowerCaseTag);


        if (tagRepository.findByTagName(lowerCaseTag) != null) {
            log.info("Tag [{}] already exists in DB", lowerCaseTag);
            return null;
        }

        tag.setTagId(Long.valueOf(new DecimalFormat("000").format(new Random().nextInt(999))));
        try {
            tagRepository.save(tag);
            log.info("Tag added to db: {}", tag.getTagId());
            return tag;
        } catch (Exception e) {
            log.info("Error Adding New Tag to DB");
            log.info("Error: {}", e.getMessage());
            return null;
        }
    }

    // Get All Tags
    public ArrayList<Tag> getAllTags() {
        log.info("Total number of tags: [{}]", tagRepository.count());

        return (ArrayList<Tag>) tagRepository.findAll();
    }

    // DELETE a Tag by Tag Id
    public int deleteTagById(Long tagId) {
        if(tagRepository.findById(tagId).isEmpty()) {
            log.info("No such tag {} exists in DB", tagId);
            return -1;
        }
        tagRepository.deleteById(tagId);
        log.info("Tag {} deleted from DB", tagId);
        return 1;
    }

    // DELETE Tag by tagName
    public int deleteTagByName(String tagName) {
        // Convert the tag name to lowercase
        String lowerCaseTagName = tagName.toLowerCase();

        if (tagRepository.findByTagName(lowerCaseTagName) != null) {
            log.info("Tag [{}] exists in DB", lowerCaseTagName);
            // Delete the tag
            tagRepository.deleteTagByName(lowerCaseTagName);
            log.info("Tag [{}] Deleted DB", lowerCaseTagName);
            return 1;
        }
        else {
            log.info("No Tag exists with Name: [{}]", lowerCaseTagName);
            return -1;
        }
    }

}
