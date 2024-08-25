package com.socialising.services.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Random;

@Table(name = "tag", uniqueConstraints = { @UniqueConstraint(columnNames = { "tagId", "tag" }) })
@Entity
@Getter
@Setter
@Builder
public class Tag implements Serializable {

    @Id
    @Column(unique=true)
    private Long tagId;

    @Column(unique=true)
    private String tag;
}
