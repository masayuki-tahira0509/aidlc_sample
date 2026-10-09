package com.example.announcementboard.service;

/**
 * お知らせの作成・更新に用いる入力値オブジェクト。
 *
 * <p>API 層のリクエストボディをサービス層へ渡すための単純な保持用クラス。
 * 検証はサーバ側（サービス層）を正とする [NFR-SEC.3]。</p>
 */
public class AnnouncementInput {

    private String title;
    private String body;
    private String author;
    private String category;

    public AnnouncementInput() {
    }

    public AnnouncementInput(String title, String body, String author, String category) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.category = category;
    }

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
