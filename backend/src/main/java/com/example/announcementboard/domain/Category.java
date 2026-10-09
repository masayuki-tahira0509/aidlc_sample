package com.example.announcementboard.domain;

import java.util.Arrays;
import java.util.Optional;

/**
 * お知らせのカテゴリ（お知らせ種別）。
 *
 * <p>許可リスト方式で、リスト外の値は拒否する [NFR-SEC.5][FR5.2]。
 * 暫定の区分値は「一般 / 重要 / 業務連絡」（確定は OQ2）。
 * 列挙型にすることで、カテゴリの追加は enum 定数の追加という
 * 局所的な変更で対応できる [NFR8 拡張容易性]。</p>
 */
public enum Category {

    GENERAL("一般"),
    IMPORTANT("重要"),
    BUSINESS("業務連絡");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    /** 画面・API で用いる日本語ラベルを返す。 */
    public String getLabel() {
        return label;
    }

    /**
     * 日本語ラベル（例: "重要"）から {@link Category} を解決する。
     * 許可リスト外の値や null/空文字は空の Optional を返す。
     *
     * @param label カテゴリの日本語ラベル
     * @return 一致する Category（なければ empty）
     */
    public static Optional<Category> fromLabel(String label) {
        if (label == null) {
            return Optional.empty();
        }
        String trimmed = label.trim();
        return Arrays.stream(values())
                .filter(c -> c.label.equals(trimmed))
                .findFirst();
    }

    /**
     * 指定ラベルが許可リストに含まれるかを判定する。
     *
     * @param label カテゴリの日本語ラベル
     * @return 許可リストに含まれる場合 true
     */
    public static boolean isAllowed(String label) {
        return fromLabel(label).isPresent();
    }
}
