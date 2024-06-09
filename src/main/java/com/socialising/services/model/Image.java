package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.text.DecimalFormat;
import java.util.Random;

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

    @Lob
    private byte[] file;

    public void setImageId() {
        this.imageId = Long.valueOf(new DecimalFormat("000000").format(new Random().nextInt(999999)));
    }
}
