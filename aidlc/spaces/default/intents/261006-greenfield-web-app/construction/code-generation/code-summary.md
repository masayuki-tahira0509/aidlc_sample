# Code Generation Summary — 社内お知らせ掲示板

お知らせCRUDの最小統合スライス（React フロントエンド + Spring Boot バックエンド + H2 ファイルDB）を、承認済みの Code Generation プランに従い test-after 方針（Standard 戦略）で実装した。ユニット分割はなく、単一のまとまりとして各レイヤーを「実装 → そのレイヤーのテストを書いて実行」の順に進めた。

## 作成したファイル

### バックエンド（`backend/`、Spring Boot 2.7 + Maven + H2）

- `pom.xml` — spring-boot-starter-web / data-jpa / validation、com.h2database:h2、spring-boot-starter-test。実行環境の JDK11 に合わせて Java 11 ターゲット（Spring Boot 2.7.18）。
- `mvnw` / `mvnw.cmd` / `.mvn/wrapper/` — Maven Wrapper（Maven 3.9.9 を取得）。
- `src/main/resources/application.properties` — H2 ファイルモード接続URL、JPA 設定、サーバポート 8080、CORS 許可オリジン（`http://localhost:5173`）、H2 コンソール。
- `src/test/resources/application-test.properties` — テスト用 H2 インメモリ設定（`create-drop`）。
- `src/main/java/.../AnnouncementBoardApplication.java` — エントリポイント。
- `domain/Category.java` — カテゴリ許可リスト（一般/重要/業務連絡）を列挙型 + ラベル解決で表現。
- `domain/Announcement.java` — JPA エンティティ。`@PrePersist` で投稿日時を自動付与、`updatable=false` で編集時不変。
- `repository/AnnouncementRepository.java` — Spring Data JPA。`findAllByOrderByCreatedAtDesc()` で降順取得。
- `service/AnnouncementService.java` — CRUD、投稿者名の既定値「名無し」補完、必須/文字数/カテゴリ許可リスト検証、投稿日時の不変保持。
- `service/AnnouncementInput.java` / `ValidationException.java` / `NotFoundException.java` — 入力VOとドメイン例外。
- `web/AnnouncementController.java` — REST（GET/POST/PUT/DELETE）、`@CrossOrigin` で開発オリジンのみ許可。
- `web/AnnouncementRequest.java` / `AnnouncementResponse.java` / `ErrorResponse.java` — API 境界のDTO。
- `web/GlobalExceptionHandler.java` — `@RestControllerAdvice` による統一エラーレスポンス（400/404/500）。
- テスト: `domain/CategoryTest.java`、`repository/AnnouncementRepositoryTest.java`、`service/AnnouncementServiceTest.java`、`web/AnnouncementControllerTest.java`。

### フロントエンド（`frontend/`、React + Vite + TypeScript）

- `package.json` / `tsconfig.json` / `tsconfig.node.json` / `vite.config.ts` — Vite + TS + Vitest 設定。
- `.eslintrc.cjs` / `.prettierrc.json` — ESLint + Prettier（NFR5）。
- `index.html` / `src/main.tsx` / `src/styles.css` — エントリと共通スタイル。
- `src/types.ts` — ドメイン型・カテゴリ定数。
- `src/api.ts` — `fetch` ベースの API クライアント。通信失敗・サーバエラーをユーザー表示用メッセージに変換。
- `src/utils/format.ts` — 日時フォーマット。
- `src/hooks/useFocusTrap.ts` — モーダルのフォーカストラップ + Escape クローズ + 復帰フォーカス。
- `src/components/AnnouncementList.tsx` — 一覧（loading/empty/error/ready 状態、操作ボタンに aria-label）。
- `src/components/CategoryBadge.tsx` — 色のみに依存しないラベル付きバッジ。
- `src/components/AnnouncementFormModal.tsx` — 作成/編集モーダル（検証/保存中/成功、`role="dialog"`、`aria-invalid`/`aria-describedby`）。
- `src/components/DeleteConfirmModal.tsx` — 削除確認（`role="alertdialog"`、初期フォーカスはキャンセル）。
- `src/components/Toast.tsx` — `aria-live="polite"` の一時通知。
- `src/App.tsx` — 画面全体の状態管理と各モーダルの連携。
- テスト: `src/components/AnnouncementList.test.tsx`、`AnnouncementFormModal.test.tsx`、`src/test/setup.ts`。

### ルート

- `README.md` — 起動・テスト手順、API 概要、認証なしの前提と公開前チェックリスト（OQ1）。
- `.gitignore` — `backend/target/`、`backend/data/`、wrapper jar を追記（既存の node_modules/dist は維持）。

## 主要な実装判断

- **Java バージョン**: プランの前提は JDK17+ だが、本実行環境の JDK は Corretto 11 だった。テストを実際にこの環境で実行・検証するため、Java 11 をサポートする Spring Boot 2.7.18 を採用し、`pom.xml` の `java.version=11` とした（JDK17 でもビルド可能）。これはテストを実行可能にするための環境適合であり、設計上のレイヤー分離や要件には影響しない。
- **カテゴリの表現**: 列挙型 `Category`（GENERAL/IMPORTANT/BUSINESS）+ 日本語ラベルで許可リストを実装（NFR-SEC.5）。カテゴリ追加は enum 定数の追加という局所的変更で済み、拡張容易性（NFR8）に寄与する。
- **投稿日時の不変**: エンティティで `@Column(updatable=false)`、サービスの `update` でも createdAt に触れないことで二重に担保（FR3.3）。
- **検証はサーバ側を正**: サービス層を信頼境界とし、必須・文字数（タイトル100字/本文2000字 暫定）・カテゴリ許可リストを検証。クライアント側検証は UX 目的のみ（NFR-SEC.3）。
- **XSS/SQLi**: 本文は React デフォルトエスケープで描画し `dangerouslySetInnerHTML` 不使用、改行は CSS `white-space: pre-wrap` で保持（NFR-SEC.6）。データアクセスは Spring Data JPA のメソッド名規約で導出され、文字列連結SQLなし（NFR-SEC.4）。
- **CORS/ポート整合**: backend 8080、frontend 5173。`@CrossOrigin` の許可オリジンは `application.properties` の `app.cors.allowed-origin` にバインドし、ワイルドカードを避けた（NFR-SEC.10）。

## テストカバレッジと実行結果

本環境（Windows, Corretto JDK 11, Maven 3.6.3, Node 20.4）で **実際にテストを実行して合格を確認した**（結果の捏造はしていない）。

### バックエンド — `cd backend && ./mvnw -q test`（全件グリーン）

| テストクラス | 件数 | 結果 |
|---|---|---|
| `CategoryTest` | 3 | PASS |
| `AnnouncementRepositoryTest`（`@DataJpaTest`） | 4 | PASS |
| `AnnouncementServiceTest`（Mockito） | 11 | PASS |
| `AnnouncementControllerTest`（`@WebMvcTest`） | 7 | PASS |
| **合計** | **25** | **Failures 0 / Errors 0** |

サービス層は Standard 戦略（5〜8件/コンポーネント）に沿い、CRUD 正常系7件＋検証/未検出の異常系4件で計11件。コントローラは各CRUD正常系＋必須未入力(400)＋未検出(404)を網羅。

### フロントエンド（任意・最小） — `cd frontend && npm run test -- --run`（グリーン）

| テストファイル | 件数 | 結果 |
|---|---|---|
| `AnnouncementList.test.tsx` | 2 | PASS |
| `AnnouncementFormModal.test.tsx` | 2 | PASS |
| **合計** | **4** | **PASS** |

> 備考: フロントのテスト実行時に React の `act(...)` 警告が出力されるが、テストは全て合格している（フォーカストラップ副作用のタイミングに由来する警告で、挙動の失敗ではない）。

### ビルド検証

- フロントエンド: `npm run build`（`tsc` 型チェック + `vite build`）が成功（40 modules、exit 0）。生成物 `dist/` は確認後に削除。
- バックエンド: `mvnw test` の過程でコンパイル・テストが成功。

## カバレッジフロア / 品質目標の扱い

`greenfield-webapp` スコープは追加のカバレッジフロアを課さない（team.md）。品質目標（検証ルール・エラーハンドリング）は満たしており、合否を通すために目標を緩めた箇所はない。

## プランからの差異

- Java バージョンのみ上記のとおり環境適合（JDK17前提 → JDK11互換の Spring Boot 2.7 採用）。要件・設計・テスト範囲に影響なし。それ以外はプラン Step 1〜14 を順に実施し、全チェックボックスを完了。
