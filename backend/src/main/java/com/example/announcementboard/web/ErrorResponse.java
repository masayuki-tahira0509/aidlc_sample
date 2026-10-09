package com.example.announcementboard.web;

import java.time.Instant;

/**
 * 統一エラーレスポンス本文。
 *
 * <p>内部実装の詳細（スタックトレース等）は含めない。ユーザー向けに安全な
 * メッセージのみを返す [code-generation-patterns: Error Messages]。</p>
 */
public class ErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final Instant timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
