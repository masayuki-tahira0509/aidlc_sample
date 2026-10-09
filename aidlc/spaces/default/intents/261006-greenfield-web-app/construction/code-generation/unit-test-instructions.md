# Unit Test Instructions — 社内お知らせ掲示板

test-after 方針（Standard 戦略）。バックエンドの主要CRUDロジックにハッピーパス単体テストを用意する。フロントのテストは任意（team-practices）で最小限。 [Testing Contract][team-practices]

## テストフレームワークと設定

### バックエンド（backend/）

- フレームワーク: JUnit 5 + Spring Boot Test（`spring-boot-starter-test`）
- DB: テストは H2 のインメモリモードを使用（本番はファイルモード）。`src/test/resources/application-test.properties` でインメモリ接続に切替
- レイヤー別: `@DataJpaTest`（リポジトリ）、サービスは通常の単体テスト、コントローラは `@WebMvcTest` もしくは `@SpringBootTest`

### フロントエンド（frontend/）

- フレームワーク: Vitest + @testing-library/react（任意・最小）
- 設定: `vitest.config.ts`

## このユニットのテスト実行コマンド（ユニットスコープ）

プロジェクト全体を無条件に回すコマンド（例: 素の `mvn test`）は使わない。本ユニットのテストに限定したコマンドを使う。

### バックエンド（最初のテスト実行より前に動作確認するコマンド）

```
cd backend && ./mvnw -q test
```

本プロジェクトは単一ユニット・単一モジュールのため、`backend/` 配下の `mvnw test` が本ユニットのテスト全体に一致する。個別テストクラスに絞る場合:

```
cd backend && ./mvnw -q -Dtest=AnnouncementServiceTest test
cd backend && ./mvnw -q -Dtest=AnnouncementRepositoryTest test
cd backend && ./mvnw -q -Dtest=AnnouncementControllerTest test
```

### フロントエンド（任意）

```
cd frontend && npm run test -- --run
```

## 期待カバレッジ目標

- 追加のカバレッジフロアは課さない（`greenfield-webapp` スコープ、お試し用途）。
- バックエンドの主要CRUD（サービス・リポジトリ・コントローラ）にハッピーパス＋必須未入力の異常系を用意（5〜8件/コンポーネントの目安）。
- 既存のテストスイートは常にグリーンに保つ。

## モック/スタブ方針

- リポジトリ層テストは `@DataJpaTest` で実DB（H2インメモリ）を使い、モックは最小限。
- サービス層テストはリポジトリをモック（Mockito）して検証ロジックを単体で確認。
- コントローラテストはサービスをモックし、HTTPステータス/エラー本文を検証。

## テストデータ管理

- 各テストはセットアップで必要なお知らせを作成し、テスト間で状態を共有しない（インメモリDBはテストごとにリセット）。
- カテゴリは暫定許可リスト（一般/重要/業務連絡）を用いる。
