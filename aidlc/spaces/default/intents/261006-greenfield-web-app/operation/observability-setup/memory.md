<!-- INVARIANT: examples are single-line HTML comments so a fresh template parses to total=0 (MEMORY_EMPTY). Do NOT un-comment or split across lines. t100 guards this. -->
> This file is kept up to date automatically while the stage runs. Add observations at the review step, not by editing here directly.

## Interpretations
<!-- example: 2026-05-29T10:14:32Z — chose REST over GraphQL; the consuming team only needs CRUD, revisit if subscriptions land -->

## Deviations
<!-- example: 2026-05-29T10:14:32Z — skipped the optional caching layer the stage prose suggested; the dataset is small enough that it adds risk -->

## Tradeoffs
<!-- example: 2026-05-29T10:14:32Z — picked TDD over BDD this run; the team is unit-first and the domain is well-understood -->

## Open questions
<!-- example: 2026-05-29T10:14:32Z — confirm the retention window with compliance before the next stage hardens the schema -->

## Interpretations
- 2026-10-07T09:20:00Z — ユーザー指示により観測性を「サービス層のトレースログ＋例外ログ」に具体化。イベントログ（起動・成功通知）は出さない。NFR の最小構成方針を、ログの中身レベルで確定した。

## Deviations
- 2026-10-07T09:20:00Z — 当初 NFR7-OBS.1 は「起動・主要CRUDイベントのログ」としていたが、ユーザー方針でイベントログを外し、サービス層トレース＋例外に一本化。業務例外（検証失敗・未検出）は WARN、トレースは DEBUG とした。

## Tradeoffs
- 2026-10-07T09:20:00Z — dashboards/alarms/slo/anomaly の各成果物は、ローカル・お試し用途のため「スコープ外」の明文化に留めた。実体のある観測性はサービス層トレースのログのみ。

## Open questions
- 2026-10-07T09:20:00Z — 将来運用移行時は Actuator メトリクス・集中ログ・SLO/アラートを検討（現状スコープ外）。

## Interpretations
- 2026-10-08T00:00:00Z — ユーザーが「最小構成に絞る」を選択（選択肢1）。前回記録の方針を踏襲し、実体ある観測性はログ中心（サービス層トレース DEBUG＋業務例外 WARN）＋ Spring Boot Actuator の `/actuator/health`。SLO/alarms/dashboards/tracing/anomaly は「ローカル・単一利用者・お試し用途のためスコープ外」を各成果物に理由付きで明記する。実装は Spring Boot 2.7.18（Actuator 未導入、ログは SLF4J 既存）。
