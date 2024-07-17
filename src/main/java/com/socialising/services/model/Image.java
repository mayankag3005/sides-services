package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image", schema="socialise", uniqueConstraints = { @UniqueConstraint(columnNames = { "imageId" }) })
@Entity
@Builder
public class Image {

    @Id
    @Column(unique=true)
    private Long imageId;

    private String filename;

    private String mimeType;

    @Lob
    private byte[] file;
}
