package com.example.announcementboard.service;

/**
 * 入力検証エラーを表すドメイン例外。
 *
 * <p>サービス層の境界で必須・文字数・許可リストの検証に失敗した場合に送出し、
 * API 層（{@code @RestControllerAdvice}）で 400 Bad Request に変換する
 * [NFR-SEC.3][FR2.6]。</p>
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
