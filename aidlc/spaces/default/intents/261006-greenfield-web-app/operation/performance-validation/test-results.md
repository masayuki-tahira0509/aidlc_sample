# Performance Test Results — 社内お知らせ掲示板

実行環境: Windows / Corretto JDK 11.0.19 / Spring Boot 2.7.18 / H2 ファイルDB（ローカル）。実行日: 2026-10-08。すべて実際にこのローカル環境で起動・計測した（捏造なし）。 [verification]

## 前提: 起動設定の不具合を1件修正

実測のため本番ローカル実行設定でバックエンドを起動したところ、起動に失敗した。

- 原因: `application.properties` の H2 接続URL `jdbc:h2:file:./data/announcement_board;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE` が、H2 2.1.214 で "Feature not supported: AUTO_SERVER=TRUE && DB_CLOSE_ON_EXIT=FALSE" となる（2.x では両オプション同時指定が不可）。
- `build-and-test` はテスト専用設定（インメモリ `create-drop`）のみで走っていたため、この本番ローカル実行設定の不具合は未検出だった。
- 対応（ユーザー承認済み）: 接続URLから `DB_CLOSE_ON_EXIT=FALSE` を削除し `jdbc:h2:file:./data/announcement_board;AUTO_SERVER=TRUE` に修正。修正後、`Started AnnouncementBoardApplication in 2.956 seconds` で正常起動を確認し、`GET /api/announcements` が 200 / 空配列を返すことを確認した。

## 計測結果（お知らせ 300 件を投入した状態）

| 項目 | 対象 | 実測（5回） | 平均 | 努力目標（目安） | 評価 |
|------|------|-------------|------|-------------------|------|
| 一覧取得 | `GET /api/announcements`（300件） | 36, 20, 10, 10, 9 ms | **17 ms** | API 200ms / 画面 1秒（NFR6.1/6.4） | ✅ 目安達成 |
| 作成 | `POST /api/announcements` | 3, 2, 2, 2, 3 ms | **2.4 ms** | API 200ms / 画面 500ms（NFR6.2/6.4） | ✅ 目安達成 |
| 削除 | `DELETE /api/announcements/{id}` | 29, 3, 3, 2, 3 ms | **8 ms** | API 200ms / 画面 500ms（NFR6.3/6.4） | ✅ 目安達成 |

補足:
- 300件の投入（POST×300）は合計 1465ms（1件あたり平均 4.9ms）で完了。
- 一覧GET・削除DELETE の初回がやや大きい（36ms / 29ms）のは JIT/初回アクセスのウォームアップによるもので、2回目以降は 10ms 前後に収束。いずれも目安（200ms/500ms/1秒）を大きく下回る。
- これらは合否基準ではなく努力目標の目安（[memory:project.md]）。全項目が目安を余裕を持って満たした。

## スケーラビリティ所見（NFR9）

- 300件の全件表示（FR1.3）でも一覧取得 17ms と破綻なし。数百件規模（NFR9.1）は現構成で問題ない。
- 件数が数千件規模に増える場合はページング後付け（NFR9.2 / OQ4）を検討する。今回の構成はそれを後付けできる余地を保っている。

## 後片付け

- バックエンドを停止。
- 計測で投入したテストデータ（`backend/data/announcement_board.mv.db`）を削除し、DBをクリーンな状態に戻した（ユーザー合意どおり、既存データに恒久的に混ぜない）。

<!-- authorize 05b0ab52 -->
