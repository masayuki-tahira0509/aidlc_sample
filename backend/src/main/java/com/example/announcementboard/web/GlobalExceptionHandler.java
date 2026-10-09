package com.example.announcementboard.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.announcementboard.service.NotFoundException;
import com.example.announcementboard.service.ValidationException;

/**
 * 統一エラーレスポンスを生成する例外ハンドラ [NFR-SEC.3]。
 *
 * <p>検証エラーは 400、未検出は 404、想定外のエラーは 500 として、本文の形式を
 * {@link ErrorResponse} に統一する。内部実装の詳細は外部へ漏らさない。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 入力検証エラー -> 400 Bad Request。 */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /** 対象未検出 -> 404 Not Found。 */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /** 想定外のエラー -> 500 Internal Server Error（詳細は漏らさない）。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "サーバ内部エラーが発生しました。");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
