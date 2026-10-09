# Build Instructions — 社内お知らせ掲示板

ローカル環境でのビルド手順。前提: JDK 11+（Corretto 等）、Node 18+（検証環境は v20.4）、Maven Wrapper 同梱。 [tech-stack-decisions]

## 前提ツール

| ツール | バージョン目安 | 用途 |
|--------|---------------|------|
| JDK | 11 以上 | バックエンド（Spring Boot 2.7） |
| Maven | Wrapper 同梱（`mvnw`/`mvnw.cmd`）のため個別インストール不要 | バックエンドビルド |
| Node.js | 18 以上 | フロントエンド（Vite） |
| npm | Node 同梱 | フロントエンド依存管理 |

## バックエンド（backend/）

```
cd backend
./mvnw.cmd clean package     # Windows（PowerShell/cmd）
# ./mvnw clean package       # macOS/Linux
```

- 成果物: `backend/target/*.jar`（実行可能 jar）
- テストを含めずビルドのみ: `./mvnw.cmd -DskipTests package`
- H2 はファイルモードで永続化（接続設定は `application.properties`）

## フロントエンド（frontend/）

```
cd frontend
npm install                  # 初回のみ
npm run build                # tsc 型チェック + vite build
```

- 成果物: `frontend/dist/`（静的アセット）
- Lint/整形: `npm run lint`（ESLint）、Prettier はエディタ統合または `npx prettier`

## 起動（開発）

```
# バックエンド（ポート 8080）
cd backend; ./mvnw.cmd spring-boot:run

# フロントエンド（ポート 5173、別ターミナル）
cd frontend; npm run dev
```

CORS はバックエンドが `http://localhost:5173` のみ許可する設定。 [security NFR-SEC.10]

## 検証（このステージで実際に実行済み）

| コマンド | 結果 |
|---------|------|
| `backend> ./mvnw.cmd -q test` | PASS（exit 0） |
| `frontend> npm run test -- --run` | PASS 4件（exit 0） |
| `frontend> npm run build` | 成功（exit 0） |

詳細は `test-results.md` を参照。
