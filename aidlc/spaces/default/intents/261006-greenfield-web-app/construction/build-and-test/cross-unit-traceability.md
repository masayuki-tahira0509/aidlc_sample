# Cross-Unit Traceability — 社内お知らせ掲示板

本プロジェクトはユニット分割がない（units-generation スキップ、単一の統合スライス）。したがって「クロスユニット」の依存関係は存在せず、要件→テストの対応を単一スコープで示す。 [requirements]

## 要件 → 検証の対応

| 要件 | 検証（テスト/手順） | 状態 |
|------|-------------------|------|
| FR1 一覧表示（降順） | `AnnouncementRepositoryTest`（降順取得）、手動結合確認 | 検証済み |
| FR2 新規登録（既定値・検証・日時自動） | `AnnouncementServiceTest`・`AnnouncementControllerTest`（POST正常/400） | 検証済み |
| FR3 編集（日時不変） | `AnnouncementServiceTest`（update で createdAt 不変） | 検証済み |
| FR4 削除（確認ダイアログ） | `AnnouncementControllerTest`（DELETE）、フロント手動確認 | 検証済み |
| FR5 データ項目・カテゴリ | `CategoryTest`（許可リスト）、エンティティマッピング | 検証済み |
| NFR-SEC.3 入力検証 | Service/Controller の必須・検証テスト | 検証済み |
| NFR-SEC.4 SQLi対策 | JPA 派生クエリ（構造的排除）、security-test 手順 | 検証済み |
| NFR-SEC.6 XSS対策 | React エスケープ（dangerouslySetInnerHTML 0件）、手動確認手順 | 検証済み |
| NFR-SEC.10 CORS | コントローラ設定、security-test 手順 | 検証済み |
| NFR7 レイヤー分離 | backend の web/service/repository/domain 分離 | 検証済み |

## クロスユニット依存

- なし（単一ユニット）。将来ユニットを分割する場合は、ここに契約（API境界）と依存を記載する。

## 残課題（完成後の整理候補）

- フロントの act() 警告解消（任意）。
- 自動E2E・自動セキュリティスキャンの導入（スコープ外、将来拡張）。
