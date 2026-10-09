# Dashboards — 社内お知らせ掲示板

## 方針

ダッシュボードは本スコープ外。ローカル・単一利用者・お試し用途のため、可視化基盤（Grafana / CloudWatch Dashboards 等）は設けない。 [observability-requirements]

## 現状の代替

- 動作確認は、アプリのコンソールログ（サービス層トレース）を手元で参照する（`log-queries.md` 参照）。
- 将来運用に移行する場合は、Spring Boot Actuator のメトリクス公開とダッシュボード導入を検討する（スコープ外）。

<!-- 2026-10-08 サマリ再確認・承認を受けて再保存（最小構成方針で確定）。 -->

<!-- authorize 36e68233 -->
