package com.socialising.services.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ChatMsgPrivateDTO {
    private String senderName;

    private String recipientName;

    private String content;

    private MultipartFile imageContent;

    private MultipartFile videoContent;
}
