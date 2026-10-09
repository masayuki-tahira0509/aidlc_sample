# Technology Stack Decisions — 社内お知らせ掲示板

## Sources

- [requirements] NFR3: フロントは React、バックは Java（Spring Boot 推奨）。NFR1: ファイルベースDB永続化。NFR2: ローカル実行。
- [Q1] 永続化DB = H2 Database（ファイルモード）を採用。
- [Q2] ビルドツール = Maven、フロント/バック分離構成（frontend/ と backend/、Maven Wrapper 同梱）。
- [team-practices] React 側 ESLint + Prettier、Java 側は言語標準フォーマッタ（任意）。test-after 方針。

## 技術選定一覧

| レイヤー | 選定 | 根拠 |
|---------|------|------|
| フロントエンド | React（TypeScript 推奨） | NFR3 で確定。型安全性で学習・保守性に資する | 
| フロントビルド | Vite + npm | 軽量・高速、React のモダン定番。学習コストが低い |
| フロント品質 | ESLint + Prettier | NFR5 / team-practices で確定 |
| バックエンド | Java + Spring Boot | NFR3 で確定。CRUD + REST の定番 |
| バックビルド | Maven（Maven Wrapper 同梱） | Q2 で確定。宣言的で学習向け。Wrapper で未インストール環境でも動作 |
| 永続化 | H2 Database（ファイルモード） | Q1 で確定。Spring Data JPA と親和、依存が軽い |
| データアクセス | Spring Data JPA | リポジトリ抽象で NFR7(b)（DB差し替え可能性）に資する |
| API 形式 | REST（JSON） | 単一エンティティCRUDに十分。C3（外部連携なし）と整合 |
| テスト（バック） | JUnit 5 + Spring Boot Test | test-after、ハッピーパス単体テスト（NFR4） |
| 構成 | モノレポ分離（frontend/ + backend/） | NFR7(a) フロント/バック分離方針に合致 |

## ADR 要約（主要な決定）

### ADR-1: 永続化に H2（ファイルモード）を採用

- **Context**: NFR1 でファイルベース永続化が必要。候補は H2 と SQLite。
- **Decision**: H2 を採用（ファイルモード）。
- **Consequences**: Spring Data JPA と追加設定なしで統合でき、依存が軽い。再起動後もデータ保持。将来 PostgreSQL 等へ移行する場合は JPA 抽象により局所的変更で済む。
- **Alternatives Rejected**: SQLite — JPA 方言ライブラリの追加設定が必要で、お試し用途には過剰。

### ADR-2: ビルドに Maven + Maven Wrapper、分離構成を採用

- **Context**: Java のビルドツールと配置方針の確定が必要。
- **Decision**: Maven を採用し `mvnw`（Wrapper）を同梱。`frontend/` と `backend/` を分離。
- **Consequences**: Maven 未インストール環境でも `./mvnw` で動作。NFR7 の分離方針に合致し、ESLint/Prettier を `frontend/` に閉じ込められる。
- **Alternatives Rejected**: Gradle — 柔軟だが学習コストが高い。単純CRUDには Maven で十分。

### ADR-3: API は REST(JSON) 単一サービス

- **Context**: 単一エンティティ・外部連携なし（C3/C4）。
- **Decision**: 1つの Spring Boot サービスが REST API を提供し、React から呼ぶ。
- **Consequences**: マイクロサービス化は不要。NFR8 の拡張容易性はデータモデル/APIの局所変更で担保。
- **Alternatives Rejected**: GraphQL — 単一エンティティCRUDには過剰。

## Assumptions & Open Questions

- [assumption] フロントは TypeScript を推奨（必須ではない。JavaScript でも可）。確定は Code Generation。
- [assumption] JDK バージョンは LTS（例: 17 以上）を想定。実行環境に合わせ Code Generation で確定。
- [assumption] H2 のファイル保存先・接続URLは `application.properties` で設定（Code Generation で記述）。
