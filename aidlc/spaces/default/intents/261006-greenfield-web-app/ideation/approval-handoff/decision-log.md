# Decision Log — Ideation フェーズ

Ideation フェーズで確定した主要な意思決定の記録。 [desc] [scope]

| # | 決定事項 | 根拠・ステージ | Source |
|---|---------|--------------|--------|
| D-1 | プロダクトは「社内お知らせ掲示板アプリ」とする | Scope Definition で確定 | [scope-document] |
| D-2 | スコープはお知らせのフルCRUD（一覧・登録・編集・削除） | 価値ある最小範囲 | [scope-document] [intent-backlog] |
| D-3 | フロントエンドは React を使用 | ユーザー指定・Feasibility | [feasibility-assessment] [constraint-register] |
| D-4 | バックエンドは Java（Spring Boot推奨）を使用 | ユーザー指定・Feasibility | [feasibility-assessment] [constraint-register] |
| D-5 | DBは H2 / SQLite（無料・簡易） | お試し用途・コスト | [feasibility-assessment] |
| D-6 | 実行環境はローカル | お試し用途 | [constraint-register] |
| D-7 | 認証・ログイン機能は含めない | 社内NW前提・お試し用途 | [scope-document] |
| D-8 | 外部システム連携は含めない | スタンドアロン方針 | [scope-document] |
| D-9 | 画面は2画面構成（一覧 + 作成/編集フォーム） | Rough Mockups で確定 | [wireframes] |
| D-10 | データ項目はタイトル・本文・投稿者名・投稿日時 | Rough Mockups で確定 | [wireframes] |
| D-11 | 開発は価値優先（一覧→登録→編集/削除→バリデーション） | Scope Definition で確定 | [intent-backlog] |

## Assumptions & Open Questions

- [assumption] 入力バリデーションの詳細（必須項目・文字数制限など）は Inception の Requirements Analysis で確定する。
