package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image", schema="socialise", uniqueConstraints = { @UniqueConstraint(columnNames = { "imageId" }) })
@Entity
@Data
public class Image {

    @Id
    @Column(unique=true)
    private Long imageId;

    private String filename;

    private String mimeType;

    private byte[] data;
}
