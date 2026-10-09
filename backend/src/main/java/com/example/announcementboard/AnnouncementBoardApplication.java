package com.example.announcementboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社内お知らせ掲示板 バックエンドのエントリポイント。
 *
 * <p>認証・認可は持たない（C1/C2）。社内ネットワーク前提かつ学習・お試し用途という
 * 明示的なスコープ判断であり、認証を実装しないことは要件である（README 参照）。</p>
 */
@SpringBootApplication
public class AnnouncementBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnouncementBoardApplication.class, args);
    }
}
