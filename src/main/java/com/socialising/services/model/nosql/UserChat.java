package com.socialising.services.model.nosql;

import com.socialising.services.constants.ChatType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "userChats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserChat {

    @Id
    private String username;

    private List<String> chats;
}
