# Team Practices — 社内お知らせ掲示板アプリ

このプロジェクトで採用する開発プラクティス。個人・お試し・学習目的の性質に合わせ、フレームワークデフォルトを基本としつつ軽量化する（確認済み）。 [scope] [evidence]

## Way of Working

トランクベース開発。`main` に直接または短命フィーチャーブランチでマージ。個人開発のため、プルリクエストは任意。 [memory:org.md#Way of Working]

## Testing Posture

- **Methodology**: test-after（実装後にテストを書く）
- **Ordering**: 各レイヤーを実装してから、そのレイヤーのテストを書いて実行する
- **カバレッジフロア**: なし（`greenfield-webapp` スコープは追加フロアを課さない。お試し用途）
- バックエンド（Java）: 主要なCRUDロジックに対しハッピーパスの単体テストを用意
- フロントエンド（React）: MVPでは必須としない（任意）

[memory:org.md#Testing Posture]

## Code Style

| 言語 | Formatter | Linter |
|------|-----------|--------|
| React (TypeScript/JavaScript) | Prettier | ESLint |
| Java | 言語標準（Google Java Format 等） | 任意 |

[memory:org.md#Code Style]

## Deployment

ローカル実行のみ（お試し段階）。本格的なデプロイパイプラインは今回のスコープ外。 [scope] [constraint-register]

## Walking Skeleton

`greenfield-webapp` スコープは `skeleton: on`。ただし単一エンティティのCRUDでユニット分割がないため、最初の動く統合スライス（お知らせ一覧表示がバックエンドAPI経由で動く）をウォーキングスケルトンとして扱う。 [memory:org.md#Walking Skeleton] [intent-backlog]

## Assumptions & Open Questions

- [assumption] 検証コマンド（アプリが実際に動くことを示すコマンド）は Code Generation 時に確定する。
