package com.example.announcementboard.web;

/**
 * お知らせ作成/編集のリクエストボディ（JSON）。
 *
 * <p>HTTP 境界で受け取る生の入力。検証はサービス層を正とする [NFR-SEC.3]。</p>
 */
public class AnnouncementRequest {

    private String title;
    private String body;
    private String author;
    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
