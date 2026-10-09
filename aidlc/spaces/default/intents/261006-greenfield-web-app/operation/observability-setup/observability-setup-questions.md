# Observability Setup — Questions

## 決定事項（ユーザーとの確認済み）

本ステージの観測性方針は、会話の中で確定した。NFR の最小構成方針（ログ中心、メトリクス/トレース/アラート/ダッシュボードはスコープ外）をベースに、ログの中身を以下に具体化した。 [observability-requirements]

- サービスロジック（`AnnouncementService` の CRUD）に**トレースログ**を残す（処理の開始/完了）。
- **例外ログ**を出す（業務例外=検証失敗・未検出は WARN）。
- 起動ログや「CRUD成功」通知などの**イベントログは不要**。
- ログ衛生を維持: メソッド名・対象ID・件数のみを出力し、タイトル・本文・投稿者名などの入力値は出さない [NFR-SEC.8]。

## Assumptions & Open Questions

- [assumption] メトリクス・分散トレーシング・アラート・ダッシュボード・SLO は本スコープ外（ローカル・単一利用者・お試し用途）。必要時に将来追加。
- [assumption] フロントエンドは開発時のブラウザコンソール出力に留める。

## Consolidated Summary Confirmation

生成（および再保存）前の最終確認。以下の観測性方針で成果物を確定します。

- サービス層（`AnnouncementService`）のCRUDにトレースログ（DEBUG、開始/完了、メソッド名・ID・件数のみ）
- 例外ログを出す（業務例外=検証失敗・未検出は WARN）。ログ衛生維持（入力値は出さない）
- イベントログ（起動・成功通知）は出さない
- メトリクス・分散トレーシング・アラート・ダッシュボード・SLO・異常検知はスコープ外（明文化のみ）

- Looks correct
- Request changes

[Answer]: Looks correct
