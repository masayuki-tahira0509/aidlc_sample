package com.example.announcementboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * カテゴリ許可リストの単体テスト [NFR-SEC.5][FR5.2]。
 */
class CategoryTest {

    @Test
    void fromLabel_resolvesAllowedLabels() {
        assertThat(Category.fromLabel("一般")).contains(Category.GENERAL);
        assertThat(Category.fromLabel("重要")).contains(Category.IMPORTANT);
        assertThat(Category.fromLabel("業務連絡")).contains(Category.BUSINESS);
    }

    @Test
    void fromLabel_rejectsUnknownOrNull() {
        assertThat(Category.fromLabel("不明なカテゴリ")).isEmpty();
        assertThat(Category.fromLabel(null)).isEmpty();
        assertThat(Category.fromLabel("")).isEmpty();
    }

    @Test
    void isAllowed_reflectsAllowList() {
        assertThat(Category.isAllowed("重要")).isTrue();
        assertThat(Category.isAllowed("緊急")).isFalse();
    }
}
