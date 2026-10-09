package com.example.announcementboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.announcementboard.domain.Announcement;

/**
 * お知らせの永続化を担うリポジトリ [NFR7(b): 永続化手段の抽象化]。
 *
 * <p>Spring Data JPA のメソッド名規約でクエリを導出するため、SQL を文字列連結で
 * 組み立てることはなく、パラメータバインディング（プリペアド文）が用いられる
 * [NFR-SEC.4]。</p>
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * 投稿日時の新しい順（降順）に全件取得する [FR1.2]。
     *
     * @return 投稿日時降順のお知らせ一覧
     */
    List<Announcement> findAllByOrderByCreatedAtDesc();
}
