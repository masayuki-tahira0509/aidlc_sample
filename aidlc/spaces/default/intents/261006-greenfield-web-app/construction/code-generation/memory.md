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
- 2026-10-07T08:40:00Z — units-generation がスコープ上スキップのため zero-Unit の stage-level 実装として1イテレーションで生成。記録は construction/code-generation/ 直下、Bolt/skeleton/swarm 儀式なし。

## Deviations
- 2026-10-07T08:40:00Z — プランは JDK17 前提だったが実行環境は Corretto JDK 11。テストを実環境で実行・検証するため JDK11 対応の Spring Boot 2.7.18 を採用（pom.xml の java.version=11）。設計・要件・テスト範囲に影響なし。code-summary.md に開示済み。

## Tradeoffs
- 2026-10-07T08:40:00Z — フロントのテストは team-practices で任意のため最小限（4件）に留め、バックエンドCRUDの単体テスト（25件）に重点を置いた。学習・お試し用途に見合うバランス。

## Open questions
- 2026-10-07T08:40:00Z — フロントテストで React act() 警告が出る（フォーカストラップ副作用のタイミング由来、失敗ではない）。将来テストを増やす際に warning 解消を検討。
- 2026-10-07T08:40:00Z — レビュー所見 R-01（未使用の allowedOrigin パラメータ）R-02（FR2.5 の一覧本文表示トレーサビリティが弱い）R-03（NFR1.1 の ID 不整合）は完成後の軽微な整理候補。
