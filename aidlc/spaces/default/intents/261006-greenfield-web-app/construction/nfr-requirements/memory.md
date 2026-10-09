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
- 2026-10-07T08:10:00Z — functional-design と units-generation がスコープ上スキップ（consumes_absent に functional-spec/rules）。ユニットDAGがないため単一スコープのNFRとして requirements.md から直接導出した。stage prose の「上流不在時は捏造せず requirements から導く」方針に従う。
- 2026-10-07T08:10:00Z — ユニット無しのため traceability.json の unit は "all"（単一ユニット扱い）とし、inception NFR1〜NFR9 を網羅対象とした。

## Deviations
- 2026-10-07T08:10:00Z — エンタープライズ級の可用性/DR/分散トレーシング等は、ローカル・単一利用者・お試し用途のため意図的にスコープ外とし、最小構成（ログ中心の観測性、努力目標の性能）で記述。NFRガイドのSLA/SLO表は参考に留めた。

## Tradeoffs
- 2026-10-07T08:10:00Z — 性能目標は厳密な percentile 基準ではなく緩やかな努力目標（Q3=A）。学習目的で合否を縛らない方針。計測可能性より開発の軽快さを優先。

## Open questions
- 2026-10-07T08:10:00Z — JDK/Maven のローカル環境有無は未確認。Code Generation で Maven Wrapper を同梱し未インストールでも動く構成とする想定。実行確認は環境依存。
