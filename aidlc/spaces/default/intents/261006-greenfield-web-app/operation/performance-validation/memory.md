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
- 2026-10-08T00:00:00Z — ユーザーが「軽量・実測ベース」（選択肢1）を選択。努力目標（NFR6.1 一覧1秒/NFR6.2,6.3 保存・削除500ms/NFR6.4 API 200ms、いずれも目安・合否不問）に対し、ローカルで軽量に実測1回を行い目安達成/未達を記録する。本格負荷試験はスコープ外。
- 2026-10-08T00:00:00Z — 実測フローは一括ゴーサインで進める合意。計測後はテストデータを削除しDBをクリーンに戻す（既存 H2 データに恒久的に混ぜない）合意。

## Deviations
- 2026-10-08T00:00:00Z — 性能実測のため本番ローカル実行設定でバックエンドを起動したところ、H2 2.1.214 で `AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE` の同時指定が "Feature not supported" となり起動失敗。build-and-test はテスト専用設定（インメモリ create-drop）のみで走っていたため、本番ローカル実行設定のこの不具合は未検出だった。ユーザー承認のもと application.properties の接続URLから `DB_CLOSE_ON_EXIT=FALSE` を削除（`jdbc:h2:file:./data/announcement_board;AUTO_SERVER=TRUE`）して起動可能にした。これは performance-validation 範囲外のコード修正だが、実測の前提として必要なため実施。

## Open questions
- 2026-10-08T00:00:00Z — この修正で build-and-test のテストに影響がないか（テストは別設定なので想定影響なし）。実測後、必要なら build-and-test の再確認を検討。
