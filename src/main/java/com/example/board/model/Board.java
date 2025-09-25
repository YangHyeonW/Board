package com.example.board.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String username;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*public Board(Long id, String title, String content, String username,Long userId, int viewCount, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }*/

}
