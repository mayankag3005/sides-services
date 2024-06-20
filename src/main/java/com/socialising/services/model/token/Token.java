package com.socialising.services.model.token;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.socialising.services.constants.TokenType;
import com.socialising.services.model.User;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
public class Token {

    @Id
    private Integer id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user;
}
