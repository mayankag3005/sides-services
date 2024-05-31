package com.socialising.services.model;

import java.io.Serializable;

public class Friend implements Serializable {

    private Long userId;

    public Friend(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
