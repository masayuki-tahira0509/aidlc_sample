package com.example.announcementboard.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.announcementboard.domain.Announcement;
import com.example.announcementboard.domain.Category;

/**
 * リポジトリ層テスト [FR1][FR3][FR4]。
 *
 * <p>{@code @DataJpaTest} で H2 インメモリの実DBを使い、保存・降順取得・更新・削除、
 * および投稿日時の自動付与（{@code @PrePersist}）を検証する。</p>
 */
@DataJpaTest
@ActiveProfiles("test")
class AnnouncementRepositoryTest {

    @Autowired
    private AnnouncementRepository repository;

    @Test
    void save_assignsIdAndAutoSetsCreatedAt() {
        // Arrange
        Announcement a = new Announcement("タイトル", "本文", "太郎", Category.GENERAL);

        // Act
        Announcement saved = repository.save(a);

        // Assert: id が採番され、投稿日時が自動付与される [FR2.3]
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void findAllByOrderByCreatedAtDesc_returnsNewestFirst() {
        // Arrange: 投稿日時を明示的にずらして3件保存
        Announcement older = new Announcement("古い", "本文1", "A", Category.GENERAL);
        older.setCreatedAt(Instant.now().minus(2, ChronoUnit.HOURS));
        Announcement middle = new Announcement("中間", "本文2", "B", Category.IMPORTANT);
        middle.setCreatedAt(Instant.now().minus(1, ChronoUnit.HOURS));
        Announcement newest = new Announcement("新しい", "本文3", "C", Category.BUSINESS);
        newest.setCreatedAt(Instant.now());
        repository.save(older);
        repository.save(middle);
        repository.save(newest);

        // Act
        List<Announcement> result = repository.findAllByOrderByCreatedAtDesc();

        // Assert: 降順 [FR1.2]
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getTitle()).isEqualTo("新しい");
        assertThat(result.get(1).getTitle()).isEqualTo("中間");
        assertThat(result.get(2).getTitle()).isEqualTo("古い");
    }

    @Test
    void update_persistsChangesAndKeepsCreatedAt() {
        // Arrange
        Announcement saved = repository.save(
                new Announcement("旧タイトル", "旧本文", "太郎", Category.GENERAL));
        Instant originalCreatedAt = saved.getCreatedAt();

        // Act: 内容を更新（createdAt は変更しない [FR3.3]）
        saved.setTitle("新タイトル");
        saved.setCategory(Category.IMPORTANT);
        repository.saveAndFlush(saved);
        Announcement reloaded = repository.findById(saved.getId()).orElseThrow();

        // Assert
        assertThat(reloaded.getTitle()).isEqualTo("新タイトル");
        assertThat(reloaded.getCategory()).isEqualTo(Category.IMPORTANT);
        assertThat(reloaded.getCreatedAt()).isEqualTo(originalCreatedAt);
    }

    @Test
    void delete_removesEntity() {
        // Arrange
        Announcement saved = repository.save(
                new Announcement("削除対象", "本文", "太郎", Category.GENERAL));
        Long id = saved.getId();

        // Act
        repository.deleteById(id);

        // Assert
        assertThat(repository.findById(id)).isEmpty();
    }
}
