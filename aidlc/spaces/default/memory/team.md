# Team-Level Rules

> This team's affirmed practices and corrections. Loaded after `org.md` as
> strict-additive guidance; contradictions with broader policy are rejected.
> Populated by the practices-discovery affirmation gate. Edit at the gate,
> not directly.

## Way of Working

トランクベース開発。`main` に直接または短命フィーチャーブランチでマージ。個人開発のため、プルリクエストは任意。 [memory:org.md#Way of Working]

## Walking Skeleton

`greenfield-webapp` スコープは `skeleton: on`。ただし単一エンティティのCRUDでユニット分割がないため、最初の動く統合スライス（お知らせ一覧表示がバックエンドAPI経由で動く）をウォーキングスケルトンとして扱う。 [memory:org.md#Walking Skeleton] [intent-backlog]

## Testing Posture

- **Methodology**: test-after
- **Ordering**: 各レイヤーを実装してから、そのレイヤーのテストを書いて実行する
- **カバレッジフロア**: なし（`greenfield-webapp` スコープは追加フロアを課さない。お試し用途）
- バックエンド（Java）: 主要なCRUDロジックに対しハッピーパスの単体テストを用意
- フロントエンド（React）: MVPでは必須としない（任意）

[memory:org.md#Testing Posture]

## Guard Policy

<!-- Affirmed by the team. Mode: strict, relaxed, or off. Strict here holds for every intent and cannot be changed from chat. A section under the retired Change Control heading, written by an earlier release, is still read. -->

## Deployment

ローカル実行のみ（お試し段階）。本格的なデプロイパイプラインは今回のスコープ外。 [scope] [constraint-register]

## Code Style

| 言語 | Formatter | Linter |
|------|-----------|--------|
| React (TypeScript/JavaScript) | Prettier | ESLint |
| Java | 言語標準（Google Java Format 等） | 任意 |

[memory:org.md#Code Style]

## Forbidden

<!-- Team-specific forbidden patterns -->

## Mandated

<!-- Team-specific mandates -->

## Corrections

<!-- Self-learning loop appends here. -->
