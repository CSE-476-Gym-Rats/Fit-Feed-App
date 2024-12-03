package com.example.fitfeed.models.dto;

import java.util.UUID;

public class PostDto {
    public int postId;
    public UUID userId;
    public String username;
    public String postText;
    public WorkoutDto workout;
    public String imageUri;
}
