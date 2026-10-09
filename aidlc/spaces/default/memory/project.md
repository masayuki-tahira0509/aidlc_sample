# Project-Level Rules

> Project-specific specialisation and corrections. Loaded after `org.md` and
> `team.md` as strict-additive guidance; contradictions with broader policy
> are rejected. Populated by practices-discovery and the self-learning loop.
>
> Use sparingly: most teams don't need a project layer. Reach for it
> only when this specific project needs stable, durable guidance beyond the
> team practice (for example, package-specific release checks or an additional
> regression suite for a legacy component).

## Way of Working

<!-- Project-specific specialisation. Example: -->
<!-- This monorepo requires package-scoped branch names and a package owner -->
<!-- review in addition to the team's normal merge policy. -->

## Walking Skeleton

<!-- Project-specific specialisation. Example: -->
<!-- The walking skeleton must exercise the legacy service adapter as well -->
<!-- as the new service boundary. -->

## Testing Posture

<!-- Project-specific specialisation. -->

## Guard Policy

<!-- Project-specific. Mode: strict, relaxed, or off. Strict here holds for every intent and cannot be changed from chat. A section under the retired Change Control heading, written by an earlier release, is still read. -->

## Deployment

<!-- Project-specific specialisation. -->

## Code Style

<!-- Project-specific specialisation. -->

## Tech Stack

<!-- Technology choices locked for this project. -->

## Decided

<!-- Decisions made in earlier stages that should not be re-asked. -->
<!-- Format: DECIDED: [decision] (Stage [slug], [date]) -->

## Scope Overrides

<!-- Custom scope rules for this project. -->

## Forbidden

<!-- Populated by practices-discovery affirmation gate. -->
<!-- Format: NEVER [behavior] (affirmed [date]) -->
<!-- Example: NEVER throw exceptions across service layer boundaries (affirmed 2026-05-17) -->

## Mandated

<!-- Populated by practices-discovery affirmation gate. -->
<!-- Format: ALWAYS [behavior] (affirmed [date]) -->
<!-- Example: ALWAYS use Result<T,E> for fallible operations in service layer (affirmed 2026-05-17) -->

## Corrections

<!-- Project-specific corrections from human feedback. -->
<!-- Format: NEVER/ALWAYS [behavior] (learned [date]) -->
- エンタープライズ級の可用性/DR/分散トレーシング等は、ローカル・単一利用者・お試し用途のため意図的にスコープ外とし、最小構成（ログ中心の観測性、努力目標の性能）で記述する。 (learned 2026-10-07) <!-- cid:261006-greenfield-web-app:nfr-requirements:4957360b8659c83c42ab0a066155f9be40699363ba1c8b8e5037cb0b3fe7e1bd -->
- 性能目標は厳密な percentile 基準ではなく緩やかな努力目標とする（学習目的のプロジェクトでは合否を縛らない）。 (learned 2026-10-07) <!-- cid:261006-greenfield-web-app:nfr-requirements:873469b07e3c6b2429c05ef1804203f78998bdded875132c406b8ea3330eedfb -->

### 進め方のルール（ユーザー合意、2026-10-07）
- ALWAYS 一歩ずつ、ユーザーの明示的なゴーサインを待ってから進める。各ステージの開始・成果物作成・コマンド実行の前に確認する。自動リマインダー（フック）に押されて先回りしない。 (learned 2026-10-07)
- NEVER ユーザーの回答を言い換えて記録しない。ゲートや確認の記録はユーザーがタイプした言葉をそのまま使う。具体化が必要なときは「こう解釈してよいか」を先に確認する。 (learned 2026-10-07)
- ALWAYS ユーザーが「止まれ」と指示したら、自動リマインダーより優先して即座に停止し、次の明示的指示まで何もしない。 (learned 2026-10-07)
- ALWAYS ファイル編集以外のコマンド（ビルド・テスト・git 等）は実行前に毎回ユーザーの承認を取る。 (learned 2026-10-07)
- ALWAYS 方針・設計の具体化はユーザーの決定とする。「推奨に任せる」と言われても、推奨を提示してユーザーが選ぶ形にし、エージェントが決めて進めない。 (learned 2026-10-07)
- ALWAYS 上記に反する事態が起き、かつエージェント自身がそれを検知できなかった場合は、実施中の手順を止めて、ユーザーと一緒に解決策を講じる。黙って続行しない。 (learned 2026-10-07)
