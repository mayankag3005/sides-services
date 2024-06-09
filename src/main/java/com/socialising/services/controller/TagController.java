package com.socialising.services.controller;

import com.socialising.services.model.Tag;
import com.socialising.services.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/tag/")
public class TagController {

    private static final Logger log = LoggerFactory.getLogger(TagController.class);
//    private final TagRepository tagRepository;

    @Autowired
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("addTag")
    public Tag addTag(@RequestBody Tag tag) {
//        tag.setTagId();
//        try {
//            this.tagRepository.save(tag);
//            log.info("Tag added to db: {}", tag.getTagId());
//            return tag;
//        } catch (Exception e) {
//            log.info(e.getMessage());
//            return null;
//        }
        return this.tagService.addTag(tag);
    }

    @GetMapping("getAllTags")
    public ArrayList<Tag> getAllTags() {
        return this.tagService.getAllTags();
    }

    @DeleteMapping("deleteTag/{tagId}")
    public int deleteTagById(@PathVariable Long tagId) {
//        if(this.tagRepository.findById(tagId).isEmpty()) {
//            log.info("No such tag {} exists in DB", tagId);
//            return -1;
//        }
//        this.tagRepository.deleteById(tagId);
//        log.info("Tag {} deleted from DB", tagId);
//        return 1;
        return this.tagService.deleteTagById(tagId);
    }

    @DeleteMapping("deleteTagByName/{tagName}")
    public void deleteTagByName(@PathVariable String tagName) {
//        if (this.tagRepository.findByTagName(tagName) != null) {
//            log.info("Tag with name {} exists in DB", tagName);
//            this.tagRepository.deleteTagByName(tagName);
//        }
//        else {
//            log.info("No Tag exists with Name: {}", tagName);
//        }
        this.tagService.deleteTagByName(tagName);
    }

}
