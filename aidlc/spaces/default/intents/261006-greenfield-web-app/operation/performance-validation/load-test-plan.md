# Load Test Plan — 社内お知らせ掲示板

## Sources

- [consumes] `construction/nfr-requirements/performance-requirements.md`（NFR6.1〜6.4 の努力目標）
- [consumes] `construction/nfr-requirements/scalability-requirements.md`（NFR9.1〜9.3、全件表示・数百件規模）
- [memory:project.md] 性能目標は厳密な percentile 基準ではなく緩やかな努力目標（合否を縛らない）

## 方針

本格的な負荷試験（同時接続・高スループット・長時間ソーク等）は本スコープ外。単一利用者・ローカル実行・お試し用途のため、努力目標が体感レベルで満たされるかを、ローカルで軽量に実測確認する。 [NFR2][scalability-requirements 非目標]

## スコープ外（明示）

- 多数同時接続・並行リクエストのスループット測定
- 負荷ツール（JMeter / k6 / Gatling 等）による継続負荷・スパイク試験
- percentile（p95/p99）ベースの SLO 判定、性能回帰ゲート

## 実施する軽量確認（Lightweight Check）

| 手順 | 内容 |
|------|------|
| 1. 起動 | `backend> ./mvnw.cmd spring-boot:run`（ローカル、ポート8080） |
| 2. データ投入 | お知らせを 300 件 POST（全件表示が破綻しない現実的上限側 NFR9.1 を想定） |
| 3. 一覧GET計測 | `GET /api/announcements`（300件）を 5 回計測し平均を記録（NFR6.1/NFR6.4） |
| 4. 作成POST計測 | `POST /api/announcements` を 5 回計測し平均を記録（NFR6.2/NFR6.4） |
| 5. 削除DELETE計測 | `DELETE /api/announcements/{id}` を 5 回計測し平均を記録（NFR6.3/NFR6.4） |
| 6. 後片付け | バックエンド停止、投入したテストデータ（H2 ファイル）を削除しDBをクリーンに戻す |

### 計測条件

- 環境: Windows / Corretto JDK 11 / Spring Boot 2.7.18 / H2 ファイルDB（ローカル）
- 計測対象はサーバ処理時間に近いローカル HTTP 往復（ネットワーク転送はローカルループバックのみ）
- 目安（合否ではない）: 一覧表示 1秒 / 保存・削除 500ms / バックエンドCRUD API 200ms

## 判定方針

数値は努力目標の「目安」であり合否を縛らない（[memory:project.md]）。各項目を「目安達成 / 未達（要観察）」で評価し、未達があっても本ステージの失敗とはしない。将来、件数が数千件規模に増えた場合はページング後付け（NFR9.2/OQ4）を検討する。

<!-- authorize 05b0ab52 -->
