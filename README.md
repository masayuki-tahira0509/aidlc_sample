# 社内お知らせ掲示板（Internal Announcement Board）

社内向けの業務効率化ツール。お知らせを1か所で閲覧・管理できる最小構成の Web アプリ（お知らせのフルCRUD）です。学習・お試しを兼ねた個人開発プロジェクトで、フロントエンドに React、バックエンドに Java / Spring Boot を採用しています。

## この資料の内容（アジェンダ）

このリポジトリの入口となる資料です。アプリの概要に加え、**フォルダ体系と「何をどう読み進めればよいか」の案内**を含みます。各セクションの要点:

- **フォルダ体系（ディレクトリ早見表）** — どのフォルダに何があり、どれが各目的の入口か
- **読み進めガイド** — まず読む3点、目的別（A〜F）の読む順、最初の一歩
- **技術構成** — 技術スタック（React / Spring Boot / H2）と層分離の考え方
- **前提** — 必要なツール（JDK 11+ / Node 18+）
- **起動手順** — バックエンド（8080）・フロントエンド（5173）の起動方法
- **テスト** — バックエンド/フロントのテスト・Lint の実行コマンド
- **API 仕様（概要）** — CRUD エンドポイントとリクエスト/エラー形式
- **セキュリティ上の前提と公開前チェックリスト** — 認証なしの前提と、社外公開前に再評価すべき項目
- **スコープ外（将来拡張候補）** — 今回は作らないと決めた機能

> 動かし方の詳細手順は `RUNBOOK.md`、開発手法は `docs/AIDLC-GUIDE.md`、意思決定の記録は `docs/DECISION-LOG.md` を参照。

---

## フォルダ体系（ディレクトリ早見表）

どのフォルダに何があるか、どれが各目的の入口（下の「読み進めガイド」A〜F に対応）かの地図です。

```
kiro_sample/
├── README.md               このファイル。最初に読む入口（概要・フォルダ体系・読み進め方）
├── RUNBOOK.md              実行手順（ビルド/起動/テスト/停止/トラブルシュート）   ← A の起点
├── ONBOARDING.md           読む順の補足案内（詳細は本 README に集約）
├── docs/
│   ├── AIDLC-GUIDE.md      AI-DLC（開発手法）の使い方ガイド                      ← E の起点
│   └── DECISION-LOG.md     協業で行った重要な意思決定の記録
├── AGENTS.md               AI-DLC の全体説明とファイル配置（ハーネス非依存）
├── backend/                Spring Boot + Spring Data JPA + H2（REST API, 8080）  ← C
│   └── src/main/java/.../  web（API）/ service（ロジック）/ domain・repository（永続化）
│   └── src/main/resources/ application.properties / data.sql（サンプル初期データ）
├── frontend/               React + Vite + TypeScript（5173）                     ← C
│   └── src/                components / api クライアント / hooks
└── aidlc/                  AI-DLC の成果物・ルール・監査ログ                      ← B/D/E/F
    └── spaces/default/
        ├── memory/         方針・ルール（org / team / project / phases）         ← F
        └── intents/261006-greenfield-web-app/
            ├── aidlc-state.md   進捗・ステージ状況
            ├── ideation/        着想（intent / feasibility / scope / mockups）
            ├── inception/       起案（requirements / refined-mockups / practices）
            ├── construction/    構築（code-generation / build-and-test / nfr）
            ├── operation/       運用（observability-setup / performance-validation）
            └── audit/           監査ログ（決定と行動の時系列）
```

> 除外（Git には含まれない）: `node_modules/`、`backend/target/`、`backend/data/`（H2 の DB ファイル）。サンプルデータは `backend/src/main/resources/data.sql` から初回起動時に自動投入されます。

---

## 読み進めガイド

### まず読む3点（5分で全体像）

1. **`README.md`（本ファイル）** — アプリの概要、フォルダ体系、API 仕様、セキュリティ前提
2. **`RUNBOOK.md`** — とにかく動かしたい人向け。ビルド・起動・停止の手順
3. **`docs/AIDLC-GUIDE.md`** — この開発がどう進められたか（AI-DLC の流れ）

### 目的別・読む順（A〜F）

上の早見表の `← A`〜`← F` と対応しています。`aidlc/.../` は `aidlc/spaces/default/intents/261006-greenfield-web-app/` の略。

**A. まず動かしたい（開発環境セットアップ）**

| 順 | 資料 | 分かること |
|----|------|-----------|
| 1 | `RUNBOOK.md` §1〜4 | 前提ツール、クイックスタート（backend 8080 / frontend 5173）、動作確認 |
| 2 | 本 README「起動手順」 | 補足の起動情報、H2 コンソール |
| 3 | `RUNBOOK.md` §8 | うまく動かないときのトラブルシューティング |

**B. 何を作ったのか知りたい（仕様・要件）**

| 順 | 資料 | 分かること |
|----|------|-----------|
| 1 | 本 README「API 仕様」ほか | 機能概要・API・セキュリティ前提 |
| 2 | `aidlc/.../inception/requirements-analysis/requirements.md` | 確定した機能要件（FR）・非機能要件（NFR） |
| 3 | `aidlc/.../ideation/rough-mockups/`（wireframes / user-flow） | 画面イメージと操作フロー |
| 4 | `aidlc/.../inception/refined-mockups/` | 精緻化したモックとアクセシビリティ観点 |

**C. コードを読みたい（実装）**

| 順 | 対象 | 内容 |
|----|------|------|
| 1 | `aidlc/.../construction/code-generation/code-summary.md` | 実装の全体像（作成ファイルと主要な設計判断の要約）。コードを読む前の地図 |
| 2 | `backend/src/main/java/.../web/` | REST コントローラ・DTO・例外ハンドラ（プレゼンテーション層） |
| 3 | `backend/src/main/java/.../service/` | CRUD・検証・既定値補完（ビジネスロジック層） |
| 4 | `backend/src/main/java/.../domain/` `repository/` | エンティティ・永続化（データアクセス層） |
| 5 | `frontend/src/` | React コンポーネント・API クライアント・フック |

バックエンドは web / service / repository の3層分離。まず `code-summary.md` を読むと迷いません。

**D. 品質・検証を知りたい（テスト・性能）**

| 資料 | 内容 |
|------|------|
| `aidlc/.../construction/build-and-test/test-results.md` | バックエンド/フロントのテスト実測結果（全グリーン） |
| `aidlc/.../construction/build-and-test/build-instructions.md` | ビルド詳細 |
| `aidlc/.../operation/performance-validation/test-results.md` | 性能実測（一覧17ms等、努力目標達成）＋起動バグ修正の記録 |
| `aidlc/.../operation/observability-setup/` | ログ（観測性）の方針 |

**E. 開発手法（AI-DLC）を理解したい**

| 順 | 資料 | 分かること |
|----|------|-----------|
| 1 | `docs/AIDLC-GUIDE.md` | AI-DLC の始め方・5フェーズ・成果物の所在・実例 |
| 2 | `AGENTS.md` | AI-DLC の全体説明とファイル配置（ハーネス非依存） |
| 3 | `.kiro/steering/aidlc-onboarding.md` | Kiro IDE 固有のオンボーディング（コマンド・前提） |
| 4 | `aidlc/.../aidlc-state.md` | 現在の進捗・各ステージの完了状況（状態ファイル） |
| 5 | `aidlc/.../audit/*.md` | 決定と行動の時系列（監査ログ） |

**F. チーム/プロジェクトのルールを知りたい**

| 資料 | 内容 |
|------|------|
| `aidlc/spaces/default/memory/project.md` | このプロジェクト固有のルール・学習（進め方の合意、性能は努力目標 等） |
| `aidlc/spaces/default/memory/team.md` | チームの合意（トランクベース開発、テスト方針、コードスタイル 等） |
| `aidlc/spaces/default/memory/org.md` | フレームワーク既定＋組織ルール |

ルールは `org → team → project` の順に重ねて適用されます（狭い層が特殊化を追加）。

### 最初の一歩（おすすめ）

1. この README を読む（5分）
2. `RUNBOOK.md` に従ってローカルで起動してみる（10分）— サンプルお知らせが13件入っています
3. `docs/AIDLC-GUIDE.md` で「どう作られたか」を把握（10分）
4. 興味に応じて上の目的別ガイド（B〜F）へ

困ったら `RUNBOOK.md` §8 のトラブルシューティング、AI-DLC の状態確認は Kiro IDE チャットで `/aidlc --status`。

---

## 技術構成

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
