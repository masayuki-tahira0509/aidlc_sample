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
- 2026-10-07T07:40:00Z — user-stories ステージがスコープ上スキップのため、ワイヤーフレーム・ユーザーフロー・要件から直接モックアップを設計した。stage prose の「missing artifact は捏造せず上流から設計する」方針に従った。

## Deviations
- 2026-10-07T07:40:00Z — ワイヤーフレームは別画面2画面構成だったが、Q1回答でフォームをモーダル化（B）に変更。一覧の文脈を保つ体験を優先。モックアップはモーダル前提で描き直した。

## Tradeoffs
- 2026-10-07T07:40:00Z — Q6でデスクトップ専用（A）、Q7でWCAG 2.1 AA目標（A）を同時選択。モバイル向け達成基準（1.4.10 Reflow等）はデスクトップ単一レイアウトでは適用範囲が限定されるが、キーボード/コントラスト/SRはデスクトップでも満たせるため両立すると判断。チェックリストにこの前提を明記した。

## Open questions
- 2026-10-07T07:40:00Z — カテゴリの具体的区分値（OQ2）とバリデーションの文字数上限（OQ5）は未確定。モックアップでは代表値（一般/重要/業務連絡、タイトル100字/本文2000字）を暫定表示とし、Functional Design/実装時に確定する旨を注記。
- 2026-10-07T07:55:00Z — 【今後の課題】レビュー所見R-01〜R-04を完成後の検討課題として記録。現状の要件では着手を妨げないが、完成後に対応要否を判断する。R-01: 保存失敗（API書込失敗）時のエラー状態モックがmockups.mdに未図示（interaction-specには定義あり）。R-02: 一覧の本文プレビューを残すか否かの確定（上流では要素だったがassumptionに格下げ）。R-03: 一覧が全件表示（ページングなし、FR1.3）であることのモック上の明示。R-04: ランドマーク`<main>`の図示をチェックリストと一致させる。いずれも現要件で問題なく進められるかを確認し、完成後に解決すべき課題か検討する。
