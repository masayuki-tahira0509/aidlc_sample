# Test Results — 社内お知らせ掲示板

このドキュメントは人間がレビューするためのテスト実行結果の記録。実行環境: Windows / Corretto JDK 11.0.19 / Node v20.4 / Maven Wrapper。すべて実際にこの環境で実行し、合否を確認した（捏造なし）。実行日時: 2026-10-07。**ドリフト助言を受けて 2026-10-08 に単独モードで再実行し、同一結果（すべてグリーン）を再確認した。** [verification]

## サマリ

| 対象 | コマンド | 結果 | Exit |
|------|---------|------|------|
| バックエンド単体/スライステスト | `backend> ./mvnw.cmd -q test` | **PASS**（失敗・エラーなし） | 0 |
| フロントエンド単体テスト | `frontend> npm run test -- --run` | **PASS** 4件（2ファイル） | 0 |
| フロントエンドビルド | `frontend> npm run build`（tsc + vite build） | **成功**（40 modules） | 0 |

総合: すべてグリーン。ビルド失敗・テスト失敗・型エラーなし。

## バックエンド詳細（JUnit 5 / Spring Boot Test）

`./mvnw.cmd -q test` を実行。`-q`（quiet）オプションのため surefire の件数サマリ行は抑制されたが、全テストクラスが起動し、失敗・エラー・例外の出力は一切なく、exit code 0 で正常終了（JPA EntityManagerFactory の正常クローズ、スキーマ drop を確認）。

実行されたテストクラス（Code Generation 時の構成と一致、計25件）:

| テストクラス | 件数 | 種別 |
|--------------|------|------|
| `CategoryTest` | 3 | 列挙型/許可リスト |
| `AnnouncementRepositoryTest` | 4 | `@DataJpaTest`（保存・降順取得・更新・削除） |
| `AnnouncementServiceTest` | 17 | Mockito（CRUD・既定値補完・検証・**境界値**） |
| `AnnouncementControllerTest` | 7 | `@WebMvcTest`（各CRUD正常系＋400/404） |
| **合計** | **31** | Failures 0 / Errors 0 |

### 境界値テスト（追加・単独再実行で補強）

当初の単体テストは「必須未入力」の異常系のみで、文字数上限の境界値が未カバーだった。`build-and-test` を `--single` モードで単独再実行し、`AnnouncementServiceTest` に以下6件の境界値テストを追加して実行・合格を確認した（本体ワークフローの位置は変更していない）。

| 観点 | ちょうど上限（OK） | 超過（NG） |
|------|-------------------|-----------|
| タイトル 100字 | `create_acceptsTitleAtMaxLength` | `create_rejectsTitleOverMaxLength`（101字） |
| 本文 2000字 | `create_acceptsBodyAtMaxLength` | `create_rejectsBodyOverMaxLength`（2001字） |
| 投稿者名 100字 | `create_acceptsAuthorAtMaxLength` | `create_rejectsAuthorOverMaxLength`（101字） |

再実行結果: `Tests run: 31, Failures: 0, Errors: 0, Skipped: 0 / BUILD SUCCESS`（exit 0）。

## フロントエンド詳細（Vitest + Testing Library）

```
Test Files  2 passed (2)
     Tests  4 passed (4)
  Duration  ~2.6s
EXITCODE=0
```

| テストファイル | 件数 | 内容 |
|----------------|------|------|
| `AnnouncementList.test.tsx` | 2 | 一覧表示・空状態 |
| `AnnouncementFormModal.test.tsx` | 2 | フォーム送信・必須検証 |

備考: 実行時に React の `act(...)` 警告が stderr に出力されるが、これはフォーカストラップの状態更新タイミングに由来する警告であり、テストは全件合格している（失敗ではない）。警告解消は任意の将来改善。

## ビルド検証

- `npm run build`（`tsc` 型チェック + `vite build`）: 成功。40 modules transformed、生成物 `dist/`（index.html 0.39kB / CSS 2.43kB / JS 152.51kB gzip 49.46kB）。検証後 `dist/` は削除。
- バックエンドは `mvnw test` の過程でコンパイル成功。

## カバレッジ/品質目標の扱い

`greenfield-webapp` スコープは追加カバレッジフロアを課さない（team.md）。品質目標を通すために目標を緩めた箇所はない。既存テストスイートはグリーンを維持。

## Loop-Back Log

（なし。Build and Test から Code Generation への差し戻しは発生していない。）

## 再実行ログ（ドリフト確認、2026-10-08）

`build-and-test` が advisory の「directly stale（ドリフト）」とマークされたため、`--stage build-and-test --single` で単独再実行し、現在のワークスペースに対して以下を実測した（本体ワークフローの位置は変更していない）。

| 対象 | コマンド | 結果 | Exit |
|------|---------|------|------|
| バックエンド | `backend> ./mvnw.cmd -q test` | **PASS**（Failures 0 / Errors 0。Hibernate のスキーマ drop/create と EntityManagerFactory の正常クローズを確認） | 0 |
| フロントエンド テスト | `frontend> npm run test -- --run` | **PASS** 4件（2ファイル。`act()` 警告のみ、失敗なし） | 0 |
| フロントエンド ビルド | `frontend> npm run build`（tsc + vite build） | **成功**（40 modules、`dist/` 生成物 index.html 0.39kB / CSS 2.43kB / JS 152.51kB gzip 49.46kB。検証後 `dist/` 削除） | 0 |

結論: 2026-10-07 の記録と**同一の結果**。ドリフトは成果物の内容に実害なし（追跡ハッシュ上のズレであり、現在のコードに対してもテスト・ビルドはすべてグリーン）。品質目標を緩めてパスさせた箇所はない。
