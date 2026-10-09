# Evidence — Practices Discovery（確認済み）

Greenfield プロジェクトのため、既存コードベースからの証拠はない。プラクティスは `org.md` のフレームワークデフォルトと、プロジェクトの性質（お試し・学習目的・個人開発）から導出する。 [scope]

## 既存コードベースの調査

| 調査対象 | 結果 | Source |
|---------|------|--------|
| 既存ソースコード | なし（Greenfield） | [workspace-detection] |
| テストスイート | なし | [workspace-detection] |
| CI設定 | なし | [workspace-detection] |
| Linter/Formatter設定 | なし | [workspace-detection] |
| ブランチ戦略の痕跡 | なし | [workspace-detection] |

## 適用するデフォルト（org.md より）

| 項目 | デフォルト | 根拠 |
|------|-----------|------|
| Way of Working | トランクベース開発 | [memory:org.md#Way of Working] |
| テスト方針 | test-after（スコープフロアなし） | [memory:org.md#Testing Posture] |
| コードスタイル | 言語標準（React=Prettier/ESLint、Java=言語標準） | [memory:org.md#Code Style] |

## Assumptions & Open Questions

- [assumption] お試し・学習目的のため、CI/CDの本格構築は最小限とする。
