package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image", schema="socialise")
@Entity
@Data
public class Image {

    public Image() {}

    public Image(Long imageId, byte[] data, String mimeType, String filename) {
        this.imageId = imageId;
        this.data = data;
        this.mimeType = mimeType;
        this.filename = filename;
    }

    @Id
    private Long imageId;

    private String filename;

    private String mimeType;

    private byte[] data;
}
