package com.socialising.services.model;

import java.io.Serializable;

public class Friend implements Serializable {

    public Friend(Long userId) {
        this.userId = userId;
    }

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
