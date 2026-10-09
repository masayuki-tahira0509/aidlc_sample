# NFR Validation Matrix — 社内お知らせ掲示板

性能・スケーラビリティ関連 NFR の検証方法と結果の対応表。数値は努力目標の目安であり合否を縛らない（[memory:project.md]）。 [consumes: performance-requirements.md, scalability-requirements.md]

## 性能（NFR6）

| NFR ID | 要件（努力目標） | 検証方法 | 実測/結果 | 評価 |
|--------|------------------|----------|-----------|------|
| NFR6.1 | 一覧表示 1秒以内を目安 | 300件投入後 `GET /api/announcements` を5回計測 | 平均 17ms（API 層） | ✅ 目安達成 |
| NFR6.2 | 保存（作成）500ms以内を目安 | `POST /api/announcements` を5回計測 | 平均 2.4ms | ✅ 目安達成 |
| NFR6.3 | 削除 500ms以内を目安 | `DELETE /api/announcements/{id}` を5回計測 | 平均 8ms | ✅ 目安達成 |
| NFR6.4 | バックエンド CRUD API 200ms以内を目安 | 上記 GET/POST/DELETE の API 応答時間 | いずれも 20ms 未満 | ✅ 目安達成 |

## スケーラビリティ（NFR9）

| NFR ID | 要件 | 検証方法 | 結果 | 評価 |
|--------|------|----------|------|------|
| NFR9.1 | MVP全件表示、数百件規模で一覧が破綻しない | 300件投入し一覧取得を計測 | 17ms で破綻なし | ✅ 達成 |
| NFR9.2 | 将来ページング/遅延読み込みを後付けできる構造 | 設計レビュー（API・データモデルの構造確認） | リポジトリ抽象＋件数非依存のレスポンス形で後付け可能 | ✅ 構造として充足（実装は将来 OQ4） |
| NFR9.3 | 項目・機能追加に局所的変更で対応（過剰抽象を避ける） | 設計レビュー（Category enum / DTO / Service の確認） | enum 追加・DTO 項目追加で局所対応可 | ✅ 充足 |

## 検出した不具合と対応

| 項目 | 内容 | 対応 |
|------|------|------|
| 本番ローカル実行設定の起動失敗 | H2 2.1.214 で `AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE` 同時指定が非対応 | `DB_CLOSE_ON_EXIT=FALSE` を削除し修正・起動確認済み（`test-results.md` 参照） |

## 総括

性能・スケーラビリティの努力目標はすべて目安を満たした。本格的な負荷試験・percentile SLO はスコープ外（ローカル・単一利用者・お試し用途）。実測の過程で本番ローカル実行設定の起動不具合を1件検出・修正した。

<!-- authorize 05b0ab52 -->
