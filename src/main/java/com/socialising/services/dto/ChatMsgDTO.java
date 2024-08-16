package com.socialising.services.dto;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMsgDTO {
    private String roomId;

    private String content;

    private MultipartFile imageContent;

    private MultipartFile videoContent;
}
