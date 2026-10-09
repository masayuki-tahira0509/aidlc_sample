package com.example.announcementboard.service;

/**
 * 指定 ID のお知らせが存在しない場合に送出する例外。
 *
 * <p>編集・削除の対象が見つからないケースで用い、API 層で 404 Not Found に
 * 変換する。</p>
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }
}
