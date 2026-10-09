# Build and Test Summary — 社内お知らせ掲示板

## 概要

Code Generation で生成した React + Spring Boot + H2 アプリを、メイン環境（Windows / Corretto JDK 11 / Node v20.4）で実際にビルド・テスト実行して検証した。すべてグリーン。ユニット分割がないため単一スコープで実施。 [verification]

## 実行結果（実測）

| 対象 | コマンド | 結果 |
|------|---------|------|
| バックエンド | `backend> ./mvnw.cmd -q test` | PASS（25件相当、失敗0） exit 0 |
| フロントエンド テスト | `frontend> npm run test -- --run` | PASS 4件 exit 0 |
| フロントエンド ビルド | `frontend> npm run build` | 成功（40 modules）exit 0 |

詳細は `test-results.md`。

## 作成した成果物

| 成果物 | 内容 |
|--------|------|
| `build-instructions.md` | ビルド・起動手順（Maven Wrapper / Vite） |
| `integration-test-instructions.md` | 結合確認手順（API↔DB、フロント↔API 手動） |
| `performance-test-instructions.md` | 性能の簡易確認（努力目標、負荷試験はスコープ外） |
| `security-test-instructions.md` | 入力検証・SQLi・XSS・CORS・秘密情報の確認手順 |
| `test-results.md` | 実測テスト結果（レビュー用） |
| `cross-unit-traceability.md` | 要件→検証の対応（単一ユニット） |
| `build-and-test-summary.md` | 本サマリ |

## 品質目標の扱い

- `greenfield-webapp` スコープは追加カバレッジフロアなし（team.md）。目標を緩めてパスさせた箇所はない。
- 既存テストスイートはグリーンを維持。

## 既知事項・残課題

- フロントの act() 警告（テストは全合格、警告のみ）。将来の任意改善。
- 自動E2E・自動セキュリティスキャンはスコープ外（必要なら CI Pipeline / 将来拡張で検討）。

## Loop-Back

Build and Test から Code Generation への差し戻しは発生していない（ルートコーズとなる失敗なし）。

## 再実行（ドリフト確認、2026-10-08）

ドリフト助言（`build-and-test` が directly stale）を受け、`--stage build-and-test --single` で単独再実行。バックエンド（`./mvnw.cmd -q test`）・フロントのテスト（`npm run test -- --run`）・フロントのビルド（`npm run build`）をすべて実測し、いずれも exit 0・グリーンで 2026-10-07 と同一結果であることを再確認した。詳細は `test-results.md` の「再実行ログ」。本体ワークフローの位置は変更していない。
