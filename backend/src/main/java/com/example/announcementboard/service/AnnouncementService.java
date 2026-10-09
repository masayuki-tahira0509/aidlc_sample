package com.example.announcementboard.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.announcementboard.domain.Announcement;
import com.example.announcementboard.domain.Category;
import com.example.announcementboard.repository.AnnouncementRepository;

/**
 * お知らせのビジネスロジック（CRUD + 検証）を担うサービス層 [NFR7(a)]。
 *
 * <p>責務:
 * <ul>
 *   <li>必須/文字数/カテゴリ許可リストの検証（サーバ側を正とする [NFR-SEC.3][NFR-SEC.5]）</li>
 *   <li>投稿者名が未入力の場合の既定値「名無し」補完 [FR2.4]</li>
 *   <li>投稿日時の自動付与（登録時）と不変保持（編集時） [FR2.3][FR3.3]</li>
 * </ul>
 * </p>
 */
@Service
public class AnnouncementService {

    /**
     * サービス層トレースログ用のロガー [Observability Setup]。
     *
     * <p>方針: サービスロジック（CRUD）の処理トレースと例外を記録する。
     * イベントログ（起動・成功通知）は出さない。ログ衛生のため、メソッド名・
     * 対象ID・件数のみを出力し、タイトル・本文・投稿者名などの入力値は
     * 出力しない [NFR-SEC.8][NFR7-OBS.3]。トレースは DEBUG、例外は ERROR。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(AnnouncementService.class);

    /** 投稿者名の既定値 [FR2.4]。 */
    static final String DEFAULT_AUTHOR = "名無し";

    /** タイトル最大文字数（暫定、確定は OQ5） [FR2.6]。 */
    static final int MAX_TITLE_LENGTH = 100;

    /** 本文最大文字数（暫定、確定は OQ5） [FR2.6]。 */
    static final int MAX_BODY_LENGTH = 2000;

    /** 投稿者名最大文字数（暫定）。 */
    static final int MAX_AUTHOR_LENGTH = 100;

    private final AnnouncementRepository repository;

    public AnnouncementService(AnnouncementRepository repository) {
        this.repository = repository;
    }

    /**
     * 全件を投稿日時の降順で取得する [FR1.1][FR1.2]。
     */
    @Transactional(readOnly = true)
    public List<Announcement> findAll() {
        log.debug("findAll: 取得開始");
        List<Announcement> result = repository.findAllByOrderByCreatedAtDesc();
        log.debug("findAll: 取得完了 count={}", result.size());
        return result;
    }

    /**
     * ID 指定で1件取得する。見つからなければ {@link NotFoundException}。
     */
    @Transactional(readOnly = true)
    public Announcement findById(Long id) {
        log.debug("findById: 取得開始 id={}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("findById: お知らせが見つかりません id={}", id);
                    return new NotFoundException("お知らせが見つかりません: id=" + id);
                });
    }

    /**
     * 新規お知らせを登録する [FR2.1]。
     *
     * <p>入力を検証し、投稿者名を補完したうえで保存する。投稿日時は
     * エンティティの {@code @PrePersist} で自動付与される [FR2.3]。</p>
     *
     * @param input 入力値
     * @return 保存されたお知らせ
     * @throws ValidationException 検証に失敗した場合
     */
    @Transactional
    public Announcement create(AnnouncementInput input) {
        log.debug("create: 登録開始");
        Category category = validateLogged(input, "create");
        Announcement announcement = new Announcement(
                input.getTitle().trim(),
                input.getBody(),
                resolveAuthor(input.getAuthor()),
                category);
        Announcement saved = repository.save(announcement);
        log.debug("create: 登録完了 id={}", saved.getId());
        return saved;
    }

    /**
     * 既存お知らせを編集する [FR3.1]。
     *
     * <p>タイトル・本文・投稿者名・カテゴリを更新する [FR3.2]。
     * 投稿日時は変更しない [FR3.3]（既存エンティティの createdAt には触れない）。</p>
     *
     * @param id    対象ID
     * @param input 入力値
     * @return 更新後のお知らせ
     * @throws NotFoundException   対象が存在しない場合
     * @throws ValidationException 検証に失敗した場合
     */
    @Transactional
    public Announcement update(Long id, AnnouncementInput input) {
        log.debug("update: 更新開始 id={}", id);
        Announcement existing = findById(id);
        Category category = validateLogged(input, "update");

        existing.setTitle(input.getTitle().trim());
        existing.setBody(input.getBody());
        existing.setAuthor(resolveAuthor(input.getAuthor()));
        existing.setCategory(category);
        // createdAt は意図的に変更しない [FR3.3]
        Announcement saved = repository.save(existing);
        log.debug("update: 更新完了 id={}", saved.getId());
        return saved;
    }

    /**
     * お知らせを削除する [FR4.1]。
     *
     * @param id 対象ID
     * @throws NotFoundException 対象が存在しない場合
     */
    @Transactional
    public void delete(Long id) {
        log.debug("delete: 削除開始 id={}", id);
        if (!repository.existsById(id)) {
            log.warn("delete: お知らせが見つかりません id={}", id);
            throw new NotFoundException("お知らせが見つかりません: id=" + id);
        }
        repository.deleteById(id);
        log.debug("delete: 削除完了 id={}", id);
    }

    // ------------------------------------------------------------------
    // 内部ヘルパー
    // ------------------------------------------------------------------

    /** 投稿者名の既定値補完 [FR2.4]。空・空白のみの場合は「名無し」。 */
    private String resolveAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return DEFAULT_AUTHOR;
        }
        return author.trim();
    }

    /**
     * {@link #validate} を呼び、検証失敗を WARN でログしてから再送出する
     * [Observability Setup]。例外メッセージはフィールド名・上限値のみを含み、
     * 入力値（本文・タイトル等）は含まないためログ衛生を保つ [NFR-SEC.8]。
     */
    private Category validateLogged(AnnouncementInput input, String op) {
        try {
            return validate(input);
        } catch (ValidationException e) {
            log.warn("{}: 入力検証に失敗 reason={}", op, e.getMessage());
            throw e;
        }
    }

    /**
     * 入力検証（必須・文字数・カテゴリ許可リスト） [NFR-SEC.3][NFR-SEC.5][FR2.6]。
     *
     * @return 解決済みの {@link Category}
     * @throws ValidationException 検証に失敗した場合
     */
    private Category validate(AnnouncementInput input) {
        if (input == null) {
            throw new ValidationException("入力がありません。");
        }

        // タイトル: 必須 + 文字数上限
        String title = input.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("タイトルは必須です。");
        }
        if (title.trim().length() > MAX_TITLE_LENGTH) {
            throw new ValidationException("タイトルは" + MAX_TITLE_LENGTH + "文字以内で入力してください。");
        }

        // 本文: 必須 + 文字数上限（改行はそのまま保持するため trim しない）
        String body = input.getBody();
        if (body == null || body.trim().isEmpty()) {
            throw new ValidationException("本文は必須です。");
        }
        if (body.length() > MAX_BODY_LENGTH) {
            throw new ValidationException("本文は" + MAX_BODY_LENGTH + "文字以内で入力してください。");
        }

        // 投稿者名: 任意だが文字数上限はある
        String author = input.getAuthor();
        if (author != null && author.trim().length() > MAX_AUTHOR_LENGTH) {
            throw new ValidationException("投稿者名は" + MAX_AUTHOR_LENGTH + "文字以内で入力してください。");
        }

        // カテゴリ: 必須 + 許可リスト
        String categoryLabel = input.getCategory();
        if (categoryLabel == null || categoryLabel.trim().isEmpty()) {
            throw new ValidationException("カテゴリは必須です。");
        }
        Optional<Category> category = Category.fromLabel(categoryLabel);
        if (!category.isPresent()) {
            throw new ValidationException("カテゴリの値が不正です: " + categoryLabel);
        }
        return category.get();
    }
}
