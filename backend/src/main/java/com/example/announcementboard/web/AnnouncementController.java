package com.example.announcementboard.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.announcementboard.domain.Announcement;
import com.example.announcementboard.service.AnnouncementInput;
import com.example.announcementboard.service.AnnouncementService;

/**
 * お知らせ CRUD の REST エンドポイント [FR1-FR4]。
 *
 * <p>CORS はフロントエンド開発オリジン（既定: http://localhost:5173）のみ許可する。
 * ワイルドカード全許可はしない [NFR-SEC.10]。入力検証エラーはサービス層が送出し、
 * {@link GlobalExceptionHandler} が 400 + 統一エラー本文へ変換する
 * [NFR-SEC.3]。</p>
 */
@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class AnnouncementController {

    private final AnnouncementService service;

    public AnnouncementController(AnnouncementService service,
                                  @Value("${app.cors.allowed-origin}") String allowedOrigin) {
        this.service = service;
    }

    /** 一覧取得（投稿日時の降順） [FR1.1][FR1.2]。 */
    @GetMapping
    public List<AnnouncementResponse> list() {
        return service.findAll().stream()
                .map(AnnouncementResponse::from)
                .collect(Collectors.toList());
    }

    /** 1件取得。 */
    @GetMapping("/{id}")
    public AnnouncementResponse get(@PathVariable Long id) {
        return AnnouncementResponse.from(service.findById(id));
    }

    /** 新規登録 [FR2.1]。成功時 201 Created。 */
    @PostMapping
    public ResponseEntity<AnnouncementResponse> create(@RequestBody AnnouncementRequest request) {
        Announcement created = service.create(toInput(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(AnnouncementResponse.from(created));
    }

    /** 編集 [FR3.1]。 */
    @PutMapping("/{id}")
    public AnnouncementResponse update(@PathVariable Long id,
                                       @RequestBody AnnouncementRequest request) {
        return AnnouncementResponse.from(service.update(id, toInput(request)));
    }

    /** 削除 [FR4.1]。成功時 204 No Content。 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AnnouncementInput toInput(AnnouncementRequest request) {
        return new AnnouncementInput(
                request.getTitle(),
                request.getBody(),
                request.getAuthor(),
                request.getCategory());
    }
}
