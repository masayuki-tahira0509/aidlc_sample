# Code Generation Plan — 社内お知らせ掲示板

## Overview

お知らせCRUDの最小統合スライス（React フロントエンド + Spring Boot バックエンド + H2 ファイルDB）を実装する。ユニット分割はなく（units-generation はスコープ上スキップ）、単一のまとまりとして実装する。test-after 方針（Standard 戦略）に従い、各レイヤーを実装してからそのレイヤーのテストを書いて実行する。 [requirements][tech-stack-decisions][team-practices]

- 構成: `frontend/`（React + Vite）と `backend/`（Spring Boot + Maven Wrapper）に分離 [NFR7][tech-stack ADR-2]
- 永続化: H2（ファイルモード）、Spring Data JPA [tech-stack ADR-1]
- API: REST(JSON)、お知らせCRUD [tech-stack ADR-3]
- 認証なし（C1/C2）。入力検証・XSS対策・CORS最小許可 [security-requirements]

## Testing Contract

```json
{
  "version": 1,
  "methodology": "test-after",
  "source": "team",
  "ordering": "各レイヤーを実装してから、そのレイヤーのテストを書いて実行する",
  "scope": "greenfield-webapp",
  "test_strategy": "standard",
  "project_type": "greenfield",
  "applicable_notes": [
    {
      "layer": "org",
      "text": "We treat tests as a first-class deliverable in every Bolt. The specific\nmethodology (TDD, BDD, ATDD, or classic test-after) is affirmed at\npractices-discovery and recorded in `team.md` under this heading with explicit\n`Methodology` and `Ordering` fields; Code Generation resolves those fields\nindependently from coverage, tooling, and scope notes.\n\nWhen no posture has been affirmed, our default per scope is:\n- **Methodology**: test-after\n- **Ordering**: implement each applicable testable layer, then write and run\n  that layer's tests.\n- `mvp`, `enterprise`, `feature`, `infra`, `classic` add an 80% line-coverage\n  floor and CI execution before merge.\n- `bugfix`, `security-patch` add a targeted regression for the specific\n  bug/vulnerability and require the existing suite to remain green.\n- `express` uses the Minimal strategy: requirement-driven unit tests (one per\n  requirement, with a happy-path floor per component); existing tests remain\n  green.\n- `poc`, `refactor`, `workshop` add no extra new-test floor and require the\n  existing suite to remain green.\n\nThe active `Test Strategy` still applies in every scope and determines test\nvolume/types. Scope floors are additive; they never reduce or replace the\nselected strategy.\n\nBuild and Test verifies defined coverage floors and affirmed quality targets;\nthey may not be weakened to make a step pass.\n\nAffirm a stricter posture in `team.md` if the team commits to one."
    },
    {
      "layer": "team",
      "text": "- **Methodology**: test-after\n- **Ordering**: 各レイヤーを実装してから、そのレイヤーのテストを書いて実行する\n- **カバレッジフロア**: なし（`greenfield-webapp` スコープは追加フロアを課さない。お試し用途）\n- バックエンド（Java）: 主要なCRUDロジックに対しハッピーパスの単体テストを用意\n- フロントエンド（React）: MVPでは必須としない（任意）\n\n[memory:org.md#Testing Posture]"
    }
  ],
  "obligations": {
    "strategy": "standard",
    "strategy_volume": [
      "Five to eight tests per component.",
      "Unit tests plus integration tests for key boundaries.",
      "Add E2E, performance, or security tests when requirements demand them."
    ],
    "scope_floor": [
      "Keep the existing test suite green.",
      "This scope adds no extra new-test floor beyond the selected test strategy."
    ],
    "combination_rule": "Apply every selected-strategy obligation and every scope-floor obligation; neither replaces the other, and a targeted scope regression may add the narrowest necessary test type beyond the strategy default."
  },
  "plan_profile": {
    "methodology": "test-after",
    "runner_step": "Bootstrap the minimal test runner/configuration and record the exact unit-scoped command.",
    "runner_ready_before_first_test": true,
    "testable_layers": [
      "Data model / database behavior",
      "Repository / data access",
      "Business logic",
      "API / endpoint",
      "Frontend behavior"
    ],
    "steps": [
      "Project structure and production configuration skeleton.",
      "Bootstrap the minimal test runner/configuration and record the exact unit-scoped command.",
      "Data model / database behavior - implement.",
      "Data model / database behavior - write and run its tests after implementation.",
      "Repository / data access - implement.",
      "Repository / data access - write and run its tests after implementation.",
      "Business logic - implement.",
      "Business logic - write and run its tests after implementation.",
      "API / endpoint - implement.",
      "API / endpoint - write and run its tests after implementation.",
      "Frontend behavior - implement.",
      "Frontend behavior - write and run its tests after implementation.",
      "Environment/build configuration.",
      "Documentation and traceability."
    ]
  },
  "input_sha256": "sha256:3adf9261925e9ff4d2b4c80ce2ad609f4748fcc6de2f0e9fca7ec89e6bb1c5bc",
  "contract_sha256": "sha256:459e613804aae2f2c44168cc7aa87dc830603ee036c213c0b3b2cf842853c332"
}
```

## Implementation Steps

ordering は test-after。各レイヤーを「実装 → そのレイヤーのテストを書いて実行」の順で進める。テストランナーのセットアップは最初のテスト実行より前に行う。

### バックエンド（backend/, Spring Boot + Maven + H2）

- [x] **Step 1: プロジェクト構造と本番設定スケルトン** — `backend/` に Maven プロジェクトを作成。`pom.xml`（spring-boot-starter-web, spring-boot-starter-data-jpa, com.h2database:h2, spring-boot-starter-test）、Maven Wrapper（`mvnw`/`mvnw.cmd`）、`src/main/resources/application.properties`（H2 ファイルモード接続URL・JPA設定・サーバポート）、メインクラス `AnnouncementBoardApplication.java`。 [tech-stack][NFR1]
- [x] **Step 2: テストランナーのブートストラップ** — `spring-boot-starter-test`（JUnit 5）が動くことを確認し、ユニット単位の実行コマンドを `unit-test-instructions.md` に記録。最初のテスト実行より前に整える。 [Testing Contract]
- [x] **Step 3: データモデル層 実装** — `Announcement` エンティティ（id, title, body, author, category, createdAt）を JPA エンティティとして定義。カテゴリは enum または許可リスト検証（一般/重要/業務連絡の暫定値）。 [FR5.1][security NFR-SEC.5]
- [x] **Step 4: データモデル層 テスト** — エンティティのマッピング/制約（必須項目、投稿日時自動付与）の単体テストを実装・実行。 [FR2.3][FR2.6]
- [x] **Step 5: リポジトリ/データアクセス層 実装** — `AnnouncementRepository`（Spring Data JPA）。投稿日時の降順取得メソッドを含む。 [FR1.2]
- [x] **Step 6: リポジトリ層 テスト** — 保存・全件降順取得・更新・削除の単体テスト（`@DataJpaTest`）を実装・実行。 [FR1][FR3][FR4]
- [x] **Step 7: ビジネスロジック層 実装** — `AnnouncementService`（CRUD、投稿者名の既定値「名無し」補完、必須/文字数バリデーション、カテゴリ許可リスト検証、投稿日時の自動付与・編集時不変）。 [FR2.4][FR2.6][FR3.3][FR5.2]
- [x] **Step 8: ビジネスロジック層 テスト** — サービスのCRUDと検証ロジック（必須未入力の拒否、既定値補完、降順）のハッピーパス単体テストを実装・実行（5〜8件/コンポーネント）。 [team-practices][Testing Contract]
- [x] **Step 9: API/エンドポイント層 実装** — `AnnouncementController`（REST: GET /api/announcements, POST, PUT /{id}, DELETE /{id}）。入力検証エラーは 400 + エラー本文、パラメータバインディング（SQLi排除）、CORS を frontend 開発オリジンのみ許可。例外ハンドラで統一エラーレスポンス。 [security NFR-SEC.3,4,10][reliability NFR1.2,1.3]
- [x] **Step 10: API層 テスト** — コントローラの単体/スライステスト（`@WebMvcTest` もしくは `@SpringBootTest`）。各CRUDの正常系＋必須未入力の異常系（400）を実装・実行。 [Testing Contract]

### フロントエンド（frontend/, React + Vite + TypeScript）

- [x] **Step 11: フロントエンド 実装** — `frontend/` に Vite + React + TypeScript プロジェクト。ESLint + Prettier 設定（`.eslintrc` / `.prettierrc`）。コンポーネント: `AnnouncementList`（一覧・空/ローディング/エラー状態）、`CategoryBadge`、`AnnouncementFormModal`（作成/編集・バリデーション/保存中/成功）、`DeleteConfirmModal`、`Toast`。API クライアント（fetch で backend 呼び出し）。本文は React デフォルトエスケープで表示（`dangerouslySetInnerHTML` 不使用）。インタラクティブ要素に `data-testid` を付与。 [refined-mockups][interaction-spec][security NFR-SEC.6][NFR5]
- [x] **Step 12: フロントエンド テスト（任意・最小）** — チーム方針でフロントのテストは MVP 必須ではない（任意）。最小限として、一覧表示とフォーム送信のハッピーパスを Vitest + Testing Library で1〜2件用意（スコープが許す範囲）。 [team-practices]

### 仕上げ

- [x] **Step 13: 環境/ビルド設定** — ルートに起動手順を記した README（backend: `./mvnw spring-boot:run`、frontend: `npm install && npm run dev`）。CORS/ポートの整合確認。認証なしの前提と公開前チェック（OQ1）を README に明記。 [security NFR-SEC.1,9]
- [x] **Step 14: ドキュメントとトレーサビリティ** — `code-summary.md`、`source-manifest.json`、`traceability.json` を作成し、各 FR/NFR を実装ファイルに対応付ける。

## Story-to-Code Traceability（要件→実装ステップ対応）

| 要件 | 実装ステップ |
|------|------------|
| FR1 お知らせ一覧表示（降順） | Step 5, 6, 9, 11 |
| FR2 新規登録（既定値・検証・日時自動） | Step 3, 7, 8, 9, 11 |
| FR3 編集（日時不変） | Step 7, 9, 11 |
| FR4 削除（確認ダイアログ） | Step 9, 11（DeleteConfirmModal） |
| FR5 データ項目・カテゴリ | Step 3, 7 |
| NFR1 永続化 | Step 1, 3, 5 |
| NFR-SEC.3,4,6,10 セキュリティ | Step 7, 9, 11 |
| NFR7 レイヤー分離・保守性 | Step 1, 5, 7, 9 |

## Assumptions & Open Questions

- [assumption] JDK は LTS（17以上）を想定。実行環境に JDK がない場合、ビルド/起動確認はユーザー環境で実施。
- [assumption] カテゴリ暫定値（一般/重要/業務連絡）と文字数上限（タイトル100字/本文2000字）は実装時の既定値。確定は OQ2/OQ5。
- [assumption] フロントのテストは任意（team-practices）。最小限に留める。
