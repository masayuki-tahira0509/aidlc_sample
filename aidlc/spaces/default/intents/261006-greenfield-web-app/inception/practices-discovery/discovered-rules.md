# Discovered Rules — 社内お知らせ掲示板アプリ（確認済み）

このプロジェクトで確認・採用したルール。すべて `org.md` のフレームワークデフォルトと矛盾しない追加・特殊化。 [evidence] [team-practices]

## Mandated（必須）

- ALWAYS バックエンドの主要CRUDロジックにはハッピーパスの単体テストを用意する（test-after） [memory:org.md#Testing Posture]
- ALWAYS React側は ESLint + Prettier を適用する [memory:org.md#Code Style]

## Forbidden（禁止）

- NEVER 認証・権限管理をこのMVPスコープに追加する（スコープ定義で除外済み） [scope-document]
- NEVER 外部システム連携をこのMVPスコープに追加する [scope-document]

## 補足

個人・お試し・学習目的のプロジェクトのため、厳格なカバレッジフロアやCIゲートは課さない。品質は主要ロジックのテストと手動確認で担保する。 [scope] [evidence]


## Assumptions & Open Questions

- None.
