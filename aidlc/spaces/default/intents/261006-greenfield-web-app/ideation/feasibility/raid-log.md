# RAID Log

RAID = Risks（リスク）、Assumptions（前提）、Issues（課題）、Dependencies（依存）

## Risks

| ID | リスク | 影響 | 可能性 | 対策 | Source |
|----|--------|------|--------|------|--------|
| R-1 | 2言語（React/Java）構成による開発・ビルドの複雑化 | 低 | 中 | モノレポ構成とシンプルなビルド手順で緩和 | [Q5] |
| R-2 | 認証なしのため、社内ネットワーク外に露出すると情報漏洩リスク | 低 | 低 | ローカル/社内限定運用を徹底 | [Q3] |

## Assumptions

| ID | 前提 | 検証タイミング | Source |
|----|------|--------------|--------|
| A-1 | バックエンドは Spring Boot を使用する | Code Generation | [Q5] |
| A-2 | DBは H2 または SQLite を使用する | Code Generation | [Q5] |
| A-3 | ポータルのコンテンツ種別は後続で確定 | Requirements Analysis | [Q1] |

## Issues

| ID | 課題 | 状態 | Source |
|----|------|------|--------|
| I-1 | 表示する具体的なコンテンツ（業務情報）が未定義 | 未解決（次ステージで解決） | [Q1] |

## Dependencies

| ID | 依存 | 種別 | Source |
|----|------|------|--------|
| D-1 | Node.js / npm（React開発環境） | 開発ツール | [Q5] |
| D-2 | JDK / Maven または Gradle（Javaビルド環境） | 開発ツール | [Q5] |

## Assumptions & Open Questions

- [assumption] 開発環境（Node.js, JDK）はローカルにセットアップ可能であることを前提とする。
