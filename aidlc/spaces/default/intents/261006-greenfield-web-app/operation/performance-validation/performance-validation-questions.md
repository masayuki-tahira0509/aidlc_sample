# Performance Validation — Questions

## 決定事項（ユーザーとの確認済み）

本ステージの性能検証方針は会話の中で確定した。 [consumes: performance-requirements.md, scalability-requirements.md]

- 方針「軽量・実測ベース」（選択肢1）: 本格負荷試験はスコープ外。ローカルで軽量に実測1回を行い、努力目標の目安達成/未達を記録する。
- 実測フローは一括ゴーサインで実施。計測後はテストデータを削除しDBをクリーンに戻す（既存データに恒久的に混ぜない）。
- 実測の過程で本番ローカル実行設定の起動不具合（H2 2.x の `AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE` 同時指定不可）を検出。ユーザー承認のもと `DB_CLOSE_ON_EXIT=FALSE` を削除して修正し、起動・実測を完了した。

## Assumptions & Open Questions

- [assumption] 性能の数値は努力目標の目安であり合否を縛らない（[memory:project.md]）。全項目が目安を達成。
- [assumption] 件数が数千件規模に達した場合は NFR9.2/OQ4 のページング導入を検討する。
- [assumption] 今回の application.properties 修正は本番ローカル実行設定のみに影響し、テスト（別設定）には影響しない想定。

## Consolidated Summary Confirmation

以下の内容で成果物を確定します。

- 軽量・実測ベースで性能検証を実施（300件投入、GET/POST/DELETE を各5回計測）
- 結果: 一覧GET 平均17ms / 作成POST 平均2.4ms / 削除DELETE 平均8ms — 努力目標の目安（API 200ms・画面500ms/1秒）をすべて達成
- 起動不具合（H2 2.x 設定）を1件検出・修正（`DB_CLOSE_ON_EXIT=FALSE` 削除）
- 本格負荷試験・percentile SLO はスコープ外（ローカル・単一利用者・お試し用途）
- 計測後にテストデータを削除しDBをクリーンに復元

- Looks correct
- Request changes

[Answer]: Looks correct
