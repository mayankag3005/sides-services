package com.socialising.services.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ChatDTO {
    private List<String> participants;

    private String roomName;

    private String type;
}
