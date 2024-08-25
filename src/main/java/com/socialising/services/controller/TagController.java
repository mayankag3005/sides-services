package com.socialising.services.controller;

import com.socialising.services.model.Tag;
import com.socialising.services.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/tag/")
public class TagController {

    private static final Logger log = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("addTag")
    @PreAuthorize("hasAuthority('admin:create')")
    public Tag addTag(@RequestBody Tag tag) {
        return this.tagService.addTag(tag);
    }

    @GetMapping("getAllTags")
    public ArrayList<Tag> getAllTags() {
        return this.tagService.getAllTags();
    }

    @DeleteMapping("deleteTag/{tagId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public int deleteTagById(@PathVariable Long tagId) {
        return this.tagService.deleteTagById(tagId);
    }

    @DeleteMapping("deleteTagByName/{tagName}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public int deleteTagByName(@PathVariable String tagName) {
        return this.tagService.deleteTagByName(tagName);
    }
}
