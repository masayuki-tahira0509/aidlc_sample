# 社内お知らせ掲示板（Internal Announcement Board）

社内向けの業務効率化ツール。お知らせを1か所で閲覧・管理できる最小構成の Web アプリ（お知らせのフルCRUD）です。学習・お試しを兼ねた個人開発プロジェクトで、フロントエンドに React、バックエンドに Java / Spring Boot を採用しています。

## この資料の内容（アジェンダ）

アプリ自体の概要・仕様・前提をまとめた資料です。各セクションの要点:

- **構成** — ディレクトリ構成と技術スタック（React / Spring Boot / H2）、層分離の考え方
- **前提** — 必要なツール（JDK 11+ / Node 18+）
- **起動手順** — バックエンド（8080）・フロントエンド（5173）の起動方法
- **テスト** — バックエンド/フロントのテスト・Lint の実行コマンド
- **API 仕様（概要）** — CRUD エンドポイントとリクエスト/エラー形式
- **セキュリティ上の前提と公開前チェックリスト** — 認証なしの前提と、社外公開前に再評価すべき項目
- **スコープ外（将来拡張候補）** — 今回は作らないと決めた機能

> 動かし方の詳細手順は `RUNBOOK.md`、読む順の案内は `ONBOARDING.md`、開発手法は `docs/AIDLC-GUIDE.md` を参照。

## 構成

```
.
├── backend/    Spring Boot + Spring Data JPA + H2（ファイルモード）REST API
└── frontend/   React + Vite + TypeScript（ESLint + Prettier）
```

- バックエンドは **プレゼンテーション層（web）／ビジネスロジック層（service）／データアクセス層（repository）** を明確に分離しています [NFR7]。
- 永続化は H2 のファイルモード。アプリ再起動後もデータは保持されます [NFR1]。
- API は REST(JSON)。フロントエンドは `fetch` で `http://localhost:8080/api/announcements` を呼び出します。

## 前提

- JDK 11 以上（Spring Boot 2.7 系。JDK 17 でも動作します）
- Node.js 18 以上 / npm

## 起動手順

### バックエンド（ポート 8080）

```bash
cd backend
./mvnw spring-boot:run      # Windows: .\mvnw.cmd spring-boot:run
```

起動すると H2 ファイル DB が `backend/data/` 配下に作成されます。開発確認用の H2 コンソールは `http://localhost:8080/h2-console`（接続URL: `jdbc:h2:file:./data/announcement_board`）。

### フロントエンド（ポート 5173）

```bash
cd frontend
npm install
npm run dev
```

ブラウザで `http://localhost:5173` を開きます。バックエンドを先に起動してください（未起動の場合は一覧画面にエラー状態が表示されます）。

## テスト

### バックエンド（JUnit 5 / Spring Boot Test）

```bash
cd backend
./mvnw -q test
```

- エンティティ／リポジトリ（`@DataJpaTest`、H2 インメモリ）、サービス（Mockito）、コントローラ（`@WebMvcTest`）の単体・スライステストを収録。
- CRUD の正常系と、必須未入力・許可外カテゴリの異常系（400）をカバーしています。

### フロントエンド（Vitest + Testing Library、任意・最小）

```bash
cd frontend
npm run test -- --run
```

### Lint / Format（フロントエンド）

```bash
cd frontend
npm run lint
npm run format
```

## API 仕様（概要）

| メソッド | パス | 説明 | 成功ステータス |
|---------|------|------|---------------|
| GET | `/api/announcements` | 一覧取得（投稿日時の降順） | 200 |
| POST | `/api/announcements` | 新規登録 | 201 |
| PUT | `/api/announcements/{id}` | 編集（投稿日時は不変） | 200 |
| DELETE | `/api/announcements/{id}` | 削除 | 204 |

リクエストボディ（POST / PUT）:

```json
{ "title": "タイトル", "body": "本文（改行可）", "author": "投稿者名（任意）", "category": "一般 | 重要 | 業務連絡" }
```

検証エラーは `400`、対象未検出は `404`、想定外エラーは `500` を、統一エラー本文（`status`, `error`, `message`, `timestamp`）で返します。

## セキュリティ上の前提と公開前チェックリスト

本アプリは **認証・認可を持ちません（C1/C2）**。これは「社内ネットワーク前提かつお試し・学習用途」という明示的なスコープ判断であり、認証を実装しないことは要件です（欠陥ではありません）[NFR-SEC.1]。

実装済みの最小限のセキュリティ対策:

- サーバ側の入力検証（必須・文字数上限・カテゴリ許可リスト）[NFR-SEC.3][NFR-SEC.5]
- Spring Data JPA のパラメータバインディングによる SQL インジェクション排除（文字列連結SQLなし）[NFR-SEC.4]
- React デフォルトエスケープによる XSS 防止（`dangerouslySetInnerHTML` 不使用。本文の改行は CSS `white-space: pre-wrap` で保持）[NFR-SEC.6]
- CORS はフロントエンド開発オリジン `http://localhost:5173` のみ許可（ワイルドカード全許可なし）[NFR-SEC.10]
- シークレットのハードコードなし [NFR-SEC.7]

### 社内ネットワーク外へ公開する前に必ず再評価する項目（OQ1）

- [ ] **認証・認可の追加**（認証方式・ロール・投稿者名のユーザー紐付けを確定）
- [ ] **TLS 化**（HTTPS）[NFR-SEC.9]
- [ ] **CORS 許可オリジンの見直し**（本番ドメインに限定）[NFR-SEC.10]
- [ ] 入力バリデーション詳細の確定（最大文字数・エラーメッセージ、OQ5）
- [ ] カテゴリ区分値の確定（OQ2）

## スコープ外（将来拡張候補）

認証・権限管理、検索・フィルタ、通知、一覧のページング、更新日時・編集履歴の管理、外部システム連携。
