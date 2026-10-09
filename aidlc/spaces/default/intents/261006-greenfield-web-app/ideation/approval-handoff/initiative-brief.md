# Initiative Brief — 社内お知らせ掲示板アプリ

Ideation フェーズの成果をまとめ、Inception フェーズへ引き継ぐためのブリーフ。 [desc] [scope]

## 1. プロダクト概要

社内の従業員が業務上のお知らせ・アナウンスを一元的に閲覧・管理できるWebアプリ。掲示板形式で、お知らせの登録・閲覧・編集・削除を行う。 [intent-statement] [scope-document]

## 2. 目的・背景

技術探索・学習を目的としたGreenfieldプロジェクト。React + Java の2層構成でWebアプリ開発を実践する。 [intent-statement] [feasibility-assessment]

## 3. ターゲットユーザー

社内の従業員・チームメンバー（認証なし・社内ネットワーク前提）。 [intent-statement] [stakeholder-map]

## 4. スコープ

### In Scope
- お知らせ一覧表示
- お知らせ新規登録
- お知らせ編集
- お知らせ削除
（＝単一エンティティのフルCRUD） [scope-document] [intent-backlog]

### Out of Scope
- 認証・ログイン
- 外部システム連携
- 検索・フィルタ
- 権限管理・通知 [scope-document]

## 5. 技術方針

| 項目 | 選定 |
|------|------|
| フロントエンド | React |
| バックエンド | Java（Spring Boot推奨） |
| データストア | H2 / SQLite（無料・簡易） |
| 実行環境 | ローカル |

[feasibility-assessment] [constraint-register]

## 6. 画面・UX

2画面構成（一覧画面 + 作成/編集フォーム）。データ項目はタイトル・本文・投稿者名・投稿日時。 [wireframes]

## 7. 成功指標

主要機能（CRUD）がリリースされ、社内ユーザーが実際に利用できる状態になること。 [intent-statement]

## 8. 主要リスク（RAIDより）

- 2言語構成の複雑化（影響：低）
- 認証なしによる露出リスク（影響：低、社内NW限定で緩和） [constraint-register]

## 9. 次フェーズ（Inception）への申し送り

- お知らせのデータ項目詳細・入力バリデーション要件を Requirements Analysis で確定する。
- 投稿者名の必須性を確定する。
- 開発プラクティス（テスト方針等）を Practices Discovery で確認する。

## Assumptions & Open Questions

- [assumption] 単一エンティティのため、Units 分割・Contract 設計は不要と判断（スコープ定義で確認済み）。
