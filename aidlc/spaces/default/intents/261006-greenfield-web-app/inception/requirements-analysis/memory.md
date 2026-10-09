<!-- INVARIANT: examples are single-line HTML comments so a fresh template parses to total=0 (MEMORY_EMPTY). Do NOT un-comment or split across lines. t100 guards this. -->
> This file is kept up to date automatically while the stage runs. Add observations at the review step, not by editing here directly.

## Interpretations
<!-- example: 2026-05-29T10:14:32Z — chose REST over GraphQL; the consuming team only needs CRUD, revisit if subscriptions land -->
- 2026-10-07T15:40:00Z — お知らせのデータ項目にカテゴリを含めた（Q1=C）。投稿者名は認証なし前提で手入力＋未入力時デフォルト補完とした。
- 2026-10-07T15:42:00Z — Q6（永続化）の初回質問が曖昧で意図が伝わらなかったため、フォローアップで「お知らせデータを再起動後も残すか」と言い換えて確認。結果ファイルDB永続化で確定。

## Deviations
<!-- example: 2026-05-29T10:14:32Z — skipped the optional caching layer the stage prose suggested; the dataset is small enough that it adds risk -->

## Tradeoffs
<!-- example: 2026-05-29T10:14:32Z — picked TDD over BDD this run; the team is unit-first and the domain is well-understood -->

## Open questions
<!-- example: 2026-05-29T10:14:32Z — confirm the retention window with compliance before the next stage hardens the schema -->
- 2026-10-07T15:50:00Z — 認証はユーザー判断で今回スコープ外（将来拡張候補 OQ1）。将来追加する際は認証方式・権限・ユーザー登録・投稿者名の紐付けを再確定する必要がある。
- 2026-10-07T15:50:00Z — 入力バリデーション要件（R-01, Major）は未確定。Functional Design または実装時に確定が必要。
