package com.socialising.services.model;

import java.sql.Timestamp;
import java.util.ArrayList;

public class ConfirmedPost {

    private Long postId;

    private Timestamp meetingTs;    // Time fixed to meet

    private ArrayList<Friend> confirmedUsers;
}