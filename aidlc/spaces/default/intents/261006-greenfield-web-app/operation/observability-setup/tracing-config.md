# Tracing Config — 社内お知らせ掲示板

## 方針

分散トレーシング（複数サービスをまたぐ W3C Trace Context 伝播等）は本スコープ外（単一サービス・外部連携なし C3）。本プロジェクトの「トレース」は、**サービスロジック層（`AnnouncementService`）の処理トレースをアプリログとして残す**ことを指す（ユーザー確定方針）。 [observability-requirements][C3]

## サービス層トレースの実装

`AnnouncementService` に SLF4J ロガーを追加し、各 CRUD メソッドの開始/完了を DEBUG で出力する。

| メソッド | トレース内容（DEBUG） | 出力項目 |
|----------|----------------------|---------|
| `findAll` | 取得開始 / 取得完了 | 件数（count） |
| `findById` | 取得開始 | 対象ID |
| `create` | 登録開始 / 登録完了 | 完了時に採番ID |
| `update` | 更新開始 / 更新完了 | 対象ID |
| `delete` | 削除開始 / 削除完了 | 対象ID |

- トレースログには**メソッド名・対象ID・件数のみ**を出力し、タイトル・本文・投稿者名などの入力値は出さない（ログ衛生 [NFR-SEC.8]）。
- 起動ログや「成功しました」系のイベントログは出さない（ユーザー方針）。

## ログレベル設定（application.properties）

```properties
logging.level.com.example.announcementboard.service=DEBUG
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
```

- サービス層のトレース（DEBUG）を表示し、フレームワークの冗長な起動/INFO ログは抑制する。

## スコープ外

- OpenTelemetry 等による分散トレーシング、トレース収集基盤、サンプリング戦略は本スコープ外。将来運用移行時に検討。

<!-- 2026-10-08 サマリ再確認・承認を受けて再保存（最小構成方針で確定）。実装 application.properties / AnnouncementService と一致を確認済み。 -->

<!-- authorize 36e68233 -->
