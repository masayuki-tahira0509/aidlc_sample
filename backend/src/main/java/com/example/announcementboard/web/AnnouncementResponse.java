package com.example.announcementboard.web;

import java.time.Instant;

import com.example.announcementboard.domain.Announcement;

/**
 * お知らせのレスポンス（JSON）。カテゴリは日本語ラベルで返す。
 */
public class AnnouncementResponse {

    private final Long id;
    private final String title;
    private final String body;
    private final String author;
    private final String category;
    private final Instant createdAt;

    public AnnouncementResponse(Long id, String title, String body, String author,
                                String category, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.category = category;
        this.createdAt = createdAt;
    }

    /** エンティティからレスポンスへ変換する。 */
    public static AnnouncementResponse from(Announcement a) {
        return new AnnouncementResponse(
                a.getId(),
                a.getTitle(),
                a.getBody(),
                a.getAuthor(),
                a.getCategory() != null ? a.getCategory().getLabel() : null,
                a.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
