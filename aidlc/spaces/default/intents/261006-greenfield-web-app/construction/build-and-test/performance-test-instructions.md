# Performance Test Instructions — 社内お知らせ掲示板

性能テストの手順。本プロジェクトは性能目標を厳密な基準値ではなく緩やかな努力目標として定義している（学習・お試し用途）。したがって自動性能テスト・負荷試験は行わず、努力目標に対する簡易な体感確認に留める。 [performance-requirements][project.md Corrections]

## 方針

- 性能目標（努力目標）: 一覧表示 1秒以内、保存/削除 500ms 以内、API 応答 200ms 以内（いずれも目安、合否を縛らない）。 [performance-requirements NFR6.1-6.4]
- 単一利用者・ローカル実行のため、同時接続・高負荷の試験は対象外。

## 簡易確認手順（手動・任意）

1. アプリを起動（build-instructions.md 参照）。
2. ブラウザの開発者ツール（Network タブ）で各操作の応答時間を目視確認:
   - 一覧取得 `GET /api/announcements`
   - 作成 `POST /api/announcements`、更新 `PUT`、削除 `DELETE`
3. 体感で努力目標の範囲内か確認する。数百件規模までは問題ない想定。

## データ量の目安

- 全件表示（ページングなし、FR1.3）。件数が数千件規模に増えた場合はページング後付けを検討（NFR9/OQ4）。 [scalability-requirements]

## スコープ外

- JMeter/Gatling 等による負荷試験、スループット計測、percentile ベースのSLO検証は本スコープ外。
