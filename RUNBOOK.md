# 実行手順書（RUNBOOK）— 社内お知らせ掲示板

ローカル環境でアプリをビルド・起動・確認・停止するための手順書です。お知らせの一覧/作成/編集/削除（CRUD）ができる、React（フロント）+ Spring Boot（バックエンド）+ H2（ファイルDB）の最小構成アプリです。学習・お試し用途・単一利用者・ローカル実行を前提にしています。

> この手順は 2026-10-08 に Windows / Corretto JDK 11 / Node v20.4 の環境で実際に起動・計測して検証した内容に基づきます。コマンド例は PowerShell 向けです（macOS/Linux の差異は各所に併記）。

## この資料の内容（アジェンダ）

アプリをビルド・起動・確認・停止するための実行手順書です。各セクションの要点:

1. **前提ツール** — JDK / Node など必要なものと確認コマンド
2. **ディレクトリ構成** — backend / frontend の配置
3. **クイックスタート** — 最短で動かす（backend 8080 → frontend 5173）
4. **動作確認** — 画面/API/H2 コンソールでの確認、API 一覧、**4.5 サンプルデータ自動投入**
5. **ビルド** — 実行可能 jar と静的アセットの作成
6. **テスト** — バックエンド/フロントのテスト・Lint
7. **停止と後片付け** — 停止方法、DB の初期化
8. **トラブルシューティング** — よくある症状と対処（CORS・ポート・H2 起動エラー 等）
9. **既知の前提・制約** — 認証なし・全件表示・性能は努力目標
10. **参考ドキュメント** — 関連資料へのリンク

---

## 1. 前提ツール

| ツール | バージョン目安 | 確認コマンド | 備考 |
|--------|---------------|--------------|------|
| JDK | 11 以上（17 でも可） | `java -version` | Spring Boot 2.7 系 |
| Node.js | 18 以上 | `node -v` | フロント（Vite） |
| npm | Node 同梱 | `npm -v` | フロント依存管理 |
| Maven | 不要（Wrapper 同梱） | — | `mvnw.cmd` を使う |

ポート **8080**（バックエンド）と **5173**（フロント）が空いていること。

---

## 2. ディレクトリ構成

```
kiro_sample/
├── backend/     Spring Boot + Spring Data JPA + H2（REST API、ポート8080）
│   ├── mvnw.cmd / mvnw            Maven Wrapper
│   ├── pom.xml
│   └── src/main/resources/application.properties
└── frontend/    React + Vite + TypeScript（ポート5173）
    └── package.json
```

---

## 3. クイックスタート（最短で動かす）

ターミナルを **2つ** 開きます。

### ターミナル1 — バックエンド（ポート 8080）

```powershell
cd backend
./mvnw.cmd spring-boot:run
```

- 初回は依存ダウンロードで時間がかかります。
- 次のログが出れば起動成功です:
  `Started AnnouncementBoardApplication in X.XXX seconds`
- 起動すると H2 ファイルDBが `backend/data/announcement_board.mv.db` に作成され、再起動後もデータは保持されます。
- **初回起動時、サンプルのお知らせが13件自動投入されます**（`backend/src/main/resources/data.sql`）。DB が空のときだけ投入される冪等SQLのため、既にデータがある場合やアプリ再起動時に重複登録されることはありません。詳しくは §4.5 を参照。

### ターミナル2 — フロントエンド（ポート 5173）

```powershell
cd frontend
npm install        # 初回のみ
npm run dev
```

- ブラウザで **http://localhost:5173** を開きます。
- バックエンドを先に起動してください。未起動だと一覧画面にエラー状態が表示されます。

---

## 4. 動作確認

### 画面から

1. http://localhost:5173 を開く → お知らせ一覧（初回は空）
2. 「新規作成」から title / body / category（一般・重要・業務連絡）を入力して保存
3. 一覧に反映 → 編集・削除も確認

### API を直接叩く（PowerShell）

```powershell
# 一覧取得
Invoke-RestMethod -Uri "http://localhost:8080/api/announcements" -Method Get

# 新規作成
$body = @{ title = "テスト"; body = "本文"; author = "山田"; category = "一般" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/api/announcements" -Method Post -Body $body -ContentType "application/json; charset=utf-8"
```

### H2 コンソール（DB の中身を見る）

- http://localhost:8080/h2-console
- 接続URL: `jdbc:h2:file:./data/announcement_board` / ユーザー: `sa` / パスワード: 空

### API 一覧

| メソッド | パス | 説明 | 成功ステータス |
|---------|------|------|---------------|
| GET | `/api/announcements` | 一覧取得（投稿日時の降順） | 200 |
| GET | `/api/announcements/{id}` | 1件取得 | 200 |
| POST | `/api/announcements` | 新規登録 | 201 |
| PUT | `/api/announcements/{id}` | 編集（投稿日時は不変） | 200 |
| DELETE | `/api/announcements/{id}` | 削除 | 204 |

リクエストボディ（POST / PUT）:

```json
{ "title": "タイトル", "body": "本文（改行可）", "author": "投稿者名（任意）", "category": "一般 | 重要 | 業務連絡" }
```

検証エラーは 400、未検出は 404、想定外は 500 を統一エラー本文（`status`, `error`, `message`, `timestamp`）で返します。

### 4.5 サンプルデータ（初回起動時に自動投入）

バックエンドを起動すると、`backend/src/main/resources/data.sql` に定義したサンプルのお知らせ **13件** が自動で投入されます。clone / コピーした環境でも、起動するだけで同じデータが表示されます。

| 項目 | 内容 |
|------|------|
| 定義ファイル | `backend/src/main/resources/data.sql`（Git で共有される＝コードの一部） |
| 投入タイミング | バックエンド起動時（Hibernate のスキーマ生成後に実行） |
| 冪等性 | `announcements` が**空のときだけ**投入。既存データがある場合・再起動時は何もしない（重複なし） |
| 内容 | カテゴリ（一般/重要/業務連絡）と投稿者を混在させた社内お知らせ風データ |

仕組み（`application.properties`）:

```properties
spring.jpa.defer-datasource-initialization=true   # スキーマ生成の後に data.sql を実行
spring.sql.init.mode=always                         # data.sql を常に評価（空のときだけ投入する冪等SQL）
```

> 注意: `data.sql` の `category` 列はエンティティの enum 名（`GENERAL` / `IMPORTANT` / `BUSINESS`）で記述します。API/画面に出る日本語ラベル（一般 / 重要 / 業務連絡）は実装側で変換されます。

#### サンプルデータを入れ直したいとき

DB を空に戻してから起動すると、再び13件が自動投入されます。

```powershell
Remove-Item -Recurse -Force c:\kiro_sample\backend\data   # DB を初期化
cd c:\kiro_sample\backend; ./mvnw.cmd spring-boot:run       # 起動 → data.sql が13件を投入
```

既存データを残したまま起動した場合は、冪等SQLにより **何も投入されません**（意図どおり）。自分で作成・編集したデータはそのまま保持されます。

---

## 5. ビルド（成果物を作る）

### バックエンド（実行可能 jar）

```powershell
cd backend
./mvnw.cmd clean package            # テスト込み
# ./mvnw.cmd -DskipTests package    # テストを省略する場合
```

- 成果物: `backend/target/*.jar`
- jar から直接起動する場合: `java -jar target/announcement-board-0.0.1-SNAPSHOT.jar`

### フロントエンド（静的アセット）

```powershell
cd frontend
npm install        # 初回のみ
npm run build      # tsc 型チェック + vite build
```

- 成果物: `frontend/dist/`

---

## 6. テスト

### バックエンド（JUnit 5 / Spring Boot Test）

```powershell
cd backend
./mvnw.cmd -q test
```

- エンティティ／リポジトリ（`@DataJpaTest`、H2 インメモリ）、サービス（Mockito）、コントローラ（`@WebMvcTest`）を収録。
- テストは**テスト専用設定**（インメモリDB）で走るため、本番ローカル実行設定とは独立しています。

### フロントエンド（Vitest + Testing Library）

```powershell
cd frontend
npm run test -- --run
```

> `--run` を付けると watch モードにならず1回実行で終了します。実行時に React の `act()` 警告が出ますが、テストは全件合格します（挙動の失敗ではありません）。

### Lint / Format（フロント）

```powershell
cd frontend
npm run lint
npm run format
```

---

## 7. 停止と後片付け

### 停止

- 各ターミナルで `Ctrl + C`。

### データを初期化したいとき（H2 ファイルを削除）

```powershell
# backend/data を削除すると、次回起動時は空のDBから始まり、data.sql のサンプル13件が再投入されます（§4.5）
Remove-Item -Recurse -Force backend/data
```

- `backend/data/announcement_board.mv.db` が永続データ本体です。消すと投入済みのお知らせは消えます。

---

## 8. トラブルシューティング

| 症状 | 原因 | 対処 |
|------|------|------|
| 一覧画面にエラー表示 | バックエンド未起動、またはポート8080で起動していない | 先にバックエンドを起動。`http://localhost:8080/api/announcements` が 200 を返すか確認 |
| フロントが CORS エラー | フロントを 5173 以外で起動した | フロントは 5173 で起動する（CORS 許可オリジンが `http://localhost:5173` のみ）。別ポートが必要なら `backend/.../application.properties` の `app.cors.allowed-origin` を合わせる |
| バックエンドが起動せず H2 の「Feature not supported」 | H2 2.x で `AUTO_SERVER=TRUE` と `DB_CLOSE_ON_EXIT=FALSE` を同時指定していると発生（本プロジェクトでは修正済み） | `application.properties` の接続URLが `jdbc:h2:file:./data/announcement_board;AUTO_SERVER=TRUE` になっているか確認（`DB_CLOSE_ON_EXIT` を含めない） |
| ポート使用中 | 8080/5173 を他プロセスが使用 | 他プロセスを停止するか、ポート設定（backend は `server.port`、frontend は `vite.config.ts`）を変更 |

---

## 9. 既知の前提・制約

- **認証・認可なし**: 社内ネットワーク前提のお試し/学習用途という明示的なスコープ判断です（欠陥ではありません）。社外公開前に認証・TLS・CORS 見直しが必要です（詳細は `README.md` の公開前チェックリスト）。
- **全件表示**: 一覧はページングなしの全件表示です。数百件規模までは快適に動作することを実測で確認済み（300件で一覧取得 約17ms）。件数が大きく増える場合はページング導入を検討してください。
- **性能は努力目標**: 一覧1秒 / 保存・削除500ms / API 200ms はいずれも合否を縛らない目安です。実測ではすべて目安を大きく下回りました（詳細は `aidlc/.../operation/performance-validation/test-results.md`）。

---

## 10. 参考ドキュメント

- `README.md` — プロジェクト概要・API 仕様・セキュリティ前提・公開前チェックリスト
- `aidlc/.../construction/build-and-test/build-instructions.md` — ビルド詳細
- `aidlc/.../construction/build-and-test/integration-test-instructions.md` — 結合確認手順
- `aidlc/.../operation/observability-setup/` — ログ（観測性）の方針
- `aidlc/.../operation/performance-validation/test-results.md` — 性能実測結果
