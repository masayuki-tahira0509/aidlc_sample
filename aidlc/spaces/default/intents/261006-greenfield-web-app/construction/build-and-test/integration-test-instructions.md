# Integration Test Instructions — 社内お知らせ掲示板

結合テスト（コンポーネント境界をまたいだ検証）の手順。本プロジェクトは単一サービス・単一エンティティ・外部連携なし（C3）のため、結合の主眼は「API ↔ サービス ↔ リポジトリ ↔ H2」と「フロント ↔ バックエンドAPI」の2経路。 [requirements]

## 1. バックエンド結合（API→DB の往復）

既存の `AnnouncementControllerTest`（`@WebMvcTest`）はコントローラ層のスライステストでサービスをモック化している。より広い結合（実DBまで通す）を確認したい場合の手順:

- `@SpringBootTest` + `@AutoConfigureMockMvc` で全層を起動し、H2 インメモリに対して CRUD を実行する結合テストを追加できる（任意・将来拡張）。
- 既存の `AnnouncementRepositoryTest`（`@DataJpaTest`）が リポジトリ↔H2 の結合を実DBで検証済み。

実行:
```
cd backend; ./mvnw.cmd -q test
```

## 2. フロント↔バックエンドの手動結合確認

自動E2Eはスコープ外（お試し用途）。手動での結合確認手順:

1. バックエンド起動: `cd backend; ./mvnw.cmd spring-boot:run`（8080）
2. フロント起動: `cd frontend; npm run dev`（5173）
3. ブラウザで `http://localhost:5173` を開く
4. 確認フロー:
   - 一覧表示（初回は空状態メッセージ）
   - 新規作成 → 保存 → 一覧に降順で表示される（FR1.2/FR2）
   - 編集 → 保存 → 内容更新・投稿日時不変（FR3.3）
   - 削除 → 確認ダイアログ → 削除 → 一覧から消える（FR4）
   - 必須未入力で保存 → バリデーションエラー表示（FR2.6）

## 期待結果

- API 応答が JSON で返り、フロントの各状態（loading/empty/error/success）が正しく遷移する。
- CORS エラーが出ないこと（許可オリジン `http://localhost:5173`）。

## スコープ外

- 自動E2E（Playwright/Cypress 等）、契約テスト、外部システム連携テストは本スコープ外（C3・お試し用途）。
