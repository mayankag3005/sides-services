package com.socialising.services.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Random;

@Table(name = "tag", uniqueConstraints = { @UniqueConstraint(columnNames = { "tagId", "tag" }) })
@Entity
public class Tag implements Serializable {

    @Id
    @Column(unique=true)
    private Long tagId;

    @Column(unique=true)
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId() {
        this.tagId = Long.valueOf(new DecimalFormat("000").format(new Random().nextInt(999)));
    }
}
