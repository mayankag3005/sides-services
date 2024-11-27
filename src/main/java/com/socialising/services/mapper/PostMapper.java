package com.socialising.services.mapper;

import com.socialising.services.dto.PostDTO;
import com.socialising.services.model.Post;

public class PostMapper {

    // Convert Post entity to PostDTO
    public static PostDTO entityToDto(Post post) {
        if (post == null) {
            return null;
        }

        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(post.getPostId());
        postDTO.setUsername(post.getOwnerUser().getUsername());
        postDTO.setDescription(post.getDescription());
        postDTO.setCreatedTs(post.getCreatedTs());
        postDTO.setPostType(post.getPostType());
        postDTO.setTimeType(post.getTimeType());
        postDTO.setPostStartTs(post.getPostStartTs());
        postDTO.setPostEndTs(post.getPostEndTs());
        postDTO.setLocation(post.getLocation());
        postDTO.setOnlyForWomen(post.getOnlyForWomen() == 'Y');  // Assuming 'Y' denotes true
        postDTO.setTags(post.getTags());
        postDTO.setHashtags(post.getHashtags());

        return postDTO;
    }

    // Convert PostDTO to Post entity
    public static Post dtoToEntity(PostDTO postDTO) {
        if (postDTO == null) {
            return null;
        }

        Post post = new Post();
        post.setDescription(postDTO.getDescription());
        post.setCreatedTs(postDTO.getCreatedTs());
        post.setPostType(postDTO.getPostType());
        post.setTimeType(postDTO.getTimeType());
        post.setPostStartTs(postDTO.getPostStartTs());
        post.setPostEndTs(postDTO.getPostEndTs());
        post.setLocation(postDTO.getLocation());
        post.setOnlyForWomen(postDTO.isOnlyForWomen() ? 'Y' : 'N');  // Map boolean to 'Y' or 'N'
        post.setTags(postDTO.getTags());
        post.setHashtags(postDTO.getHashtags());

        return post;
    }
}

