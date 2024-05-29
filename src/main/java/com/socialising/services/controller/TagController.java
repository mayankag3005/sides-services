package com.socialising.services.controller;

import com.socialising.services.model.Tag;
import com.socialising.services.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/tag/")
@Slf4j
public class TagController {

    private static final Logger log = LoggerFactory.getLogger(TagController.class);
    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping("getAllTags")
    public ArrayList<Tag> getAllTags() {
        log.info("Total number of tags: {}", this.tagRepository.count());

        return (ArrayList<Tag>) this.tagRepository.findAll();
    }

    @PostMapping("addTag")
    public Tag addTag(@RequestBody Tag tag) {
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

    @DeleteMapping("deleteTag/{tagId}")
    public int deleteTagById(@PathVariable Long tagId) {
        if(this.tagRepository.findById(tagId).isEmpty()) {
            log.info("No such tag {} exists in DB", tagId);
            return -1;
        }
        this.tagRepository.deleteById(tagId);
        log.info("Tag {} deleted from DB", tagId);
        return 1;
    }

}
