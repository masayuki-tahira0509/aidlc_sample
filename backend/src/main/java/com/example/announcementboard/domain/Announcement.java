package com.example.announcementboard.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/**
 * お知らせエンティティ [FR5.1]。
 *
 * <p>保持する項目: id, タイトル, 本文, 投稿者名, カテゴリ, 投稿日時。
 * 投稿日時（createdAt）は登録時にシステムが自動付与し [FR2.3]、
 * 編集時には変更しない [FR3.3]（更新処理では createdAt を触らない）。</p>
 */
@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** タイトル（必須、最大100字） [FR2.6] */
    @Column(nullable = false, length = 100)
    private String title;

    /** 本文（必須、最大2000字、改行を保持） [FR2.5][FR2.6] */
    @Column(nullable = false, length = 2000)
    private String body;

    /** 投稿者名（任意、未入力時は既定値「名無し」をサービス層で補完） [FR2.4] */
    @Column(nullable = false, length = 100)
    private String author;

    /** カテゴリ（必須、許可リスト） [FR5.2][NFR-SEC.5] */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    /** 投稿日時（登録時に自動付与、編集時は不変） [FR2.3][FR3.3] */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /** JPA 用のデフォルトコンストラクタ。 */
    protected Announcement() {
    }

    public Announcement(String title, String body, String author, Category category) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.category = category;
    }

    /**
     * 永続化直前に投稿日時を自動付与する [FR2.3]。
     * 既に値が設定されている場合は尊重する（テスト等での明示設定を許容）。
     */
    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /** テスト等で投稿日時を明示設定するための setter（通常は PrePersist が自動付与）。 */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
