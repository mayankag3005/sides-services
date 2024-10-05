package com.socialising.services.mapper;

import com.socialising.services.dto.CommentDTO;
import com.socialising.services.dto.CommentResponseDTO;
import com.socialising.services.model.Comment;

public class CommentMapper {

    // Convert Comment entity to CommentResponseDTO
    public static CommentResponseDTO entityToDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setDescription(comment.getDescription());
        commentResponseDTO.setUsername(comment.getUsername());
        commentResponseDTO.setCommentLikes(comment.getCommentLikes());

        return commentResponseDTO;
    }

    // Convert CommentDTO to Comment entity
    public static Comment dtoToEntity(CommentDTO commentDTO) {
        if (commentDTO == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setDescription(commentDTO.getDescription());

        return comment;
    }
}
