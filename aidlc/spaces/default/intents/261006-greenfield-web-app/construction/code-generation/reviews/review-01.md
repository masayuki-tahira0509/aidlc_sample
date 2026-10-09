## Review

**Reviewer:** aidlc-architecture-reviewer-agent
**Verdict:** READY
**Iteration:** 1

アーキテクチャ・レビュー（code-generation、敵対的レビュー 第1イテレーション）。source-manifest.json に列挙された実装ファイルを差分的に開き、FR1-FR5 / NFR-SEC / Construction ガードレール / Testing Contract / プラン差異を実コードに照らして検証した。ブロッキング所見はない。READY は、参照破綻・SQL連結・XSS 経路・CORS ワイルドカード・秘密情報ハードコード・偽テストを能動的に探索して**発見できなかった**結果である。

### 検証した主な事実（根拠）

- **FR 網羅**: traceability.json の全 OK ターゲットが実在し、主張どおり実装されていることを確認した。FR1.1/1.2（`AnnouncementController.list` → `AnnouncementService.findAll` → `AnnouncementRepository.findAllByOrderByCreatedAtDesc`、降順）、FR2.3/3.3（`Announcement.@PrePersist` + `@Column(updatable=false)`、`update` は createdAt 不改変）、FR2.4（`resolveAuthor` の「名無し」補完）、FR2.6/NFR-SEC.3/5（`AnnouncementService.validate` の必須・文字数・カテゴリ許可リスト、サーバ側を正）、FR4.2（`DeleteConfirmModal` の確認ダイアログ）いずれも実コードで追跡可能。
- **Testing Contract**: test-after、バックエンド CRUD にハッピーパス＋異常系（400/404/検証失敗）が存在。`AnnouncementServiceTest`・`AnnouncementControllerTest`・`AnnouncementRepositoryTest`・`CategoryTest` は意味のあるアサーション（降順順序、createdAt 不変、既定値補完、`never().save()`、HTTP ステータス＋エラー本文）を行っており、`assertTrue(true)` 類の偽テストは無い。`@WebMvcTest` は `@RestControllerAdvice`（`GlobalExceptionHandler`）を取り込むため、400/404 の主張は構造的に成立する。
- **セキュリティ**: データアクセスは Spring Data JPA の派生クエリのみで文字列連結 SQL は無い（NFR-SEC.4）。フロント全体を grep して `dangerouslySetInnerHTML` の使用は 0 件（NFR-SEC.6）。CORS は `@CrossOrigin(origins = "${app.cors.allowed-origin}")` + `application.properties` の単一オリジンにバインドされ、ワイルドカードではない（NFR-SEC.10）。認証情報・API キーのハードコードは無く、H2 のローカルファイル DB の空パスワードは標準的な学習用構成で受容可能。認証なしは C1/C2/NFR-SEC.1-2 の明示的スコープ判断。
- **Construction ガードレール**: 生成ファイルは実行可能で未完スタブ無し。統合境界でのエラーハンドリングを確認（`api.ts` の `request`/`parseResponse` が通信失敗・サーバエラーをユーザー向けメッセージへ変換、`GlobalExceptionHandler` が 400/404/500 を統一本文へ）。黙殺される失敗は見られない。
- **プラン差異（JDK17→JDK11 / Spring Boot 2.7.18）**: code-summary.md と pom.xml コメントで開示済み。Spring Boot 2.7.18 は Java 11 をサポートし JDK17 でもビルド可能で、レイヤー分離・要件・テスト範囲に影響しない。健全で開示された環境適合であり欠陥ではない。

### Findings

| ID | Severity | Finding | Recommendation |
|---|---|---|---|
| R-01 | Minor | `AnnouncementController` のコンストラクタが `@Value("${app.cors.allowed-origin}") String allowedOrigin` を受け取るが本体で未使用（デッドコード）。実際の CORS はクラスの `@CrossOrigin` アノテーションのプレースホルダ経由で機能しており動作上の欠陥はない。 | 混乱回避のため未使用パラメータを削除する（任意、ブロッキングではない）。 |
| R-02 | Minor | traceability.json が FR2.5 を `frontend/src/styles.css` に対応付けているが、`white-space: pre-wrap` は入力用 `.field textarea` に適用されており、保存済み本文を改行保持して表示する「表示ビュー」ではない。FR2.5 本体（複数行入力・改行を保持して保存）はエンティティ/サービスが本文を trim せず保持することで実質満たされ、一覧での本文表示はモックアップ上「任意・折りたたみ可」[assumption] のため欠落は許容範囲。トレーサビリティ・ターゲットがやや弱い点のみ。 | 将来本文詳細表示を追加する際は `pre-wrap` を本文表示要素にも適用し、FR2.5 のトレーサビリティ・ターゲットをその表示ビューへ更新する（任意）。 |
| R-03 | Minor（suggestion） | `traceability.json` に upstream_ids 未掲載の `NFR1.1` が coverage に出現する（upstream は `NFR1` のみ）。実害はないが ID の整合性観点で軽微な不一致。 | upstream_ids と coverage の NFR1 系 ID を一致させる（任意）。 |

ブロッキング所見（Critical / 過半の Major）は無い。R-01〜R-03 はいずれも Minor であり、READY を妨げない。実装はプランと上流コントラクトに忠実で、開発者が追加のアーキテクチャ指示なしにビルド・運用できる水準にある。
