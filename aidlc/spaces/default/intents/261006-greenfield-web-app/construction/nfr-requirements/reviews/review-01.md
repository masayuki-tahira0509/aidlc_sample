## Review

**Reviewer:** aidlc-architecture-reviewer-agent
**Verdict:** READY
**Iteration:** 1

本レビューは `security-requirements.md` を対象とする敵対的レビュー（1回目）である。no-auth/no-authz 前提（C1/C2）、入力検証・SQLi・XSS 対策、上流エビデンスとの整合、トレーサビリティ網羅、ローカル前提下でも意味のあるセキュリティギャップ、成果物間の矛盾を重点的に検証した。欠陥の作り込みを前提に反証を試みたが、READY を覆す Critical / 複数 Major は検出されなかった。

### 評価の要点（反証を試みた結果）

- **no-auth/no-authz は明示的スコープ判断として扱われている（欠陥ではない）**: 「セキュリティ方針」節および NFR-SEC.1/NFR-SEC.2 が C1/C2 を根拠に「認証を実装しないことは要件であって欠陥ではない」と明記し、A2（投稿者名の本人性非保証）を受容リスクとして記録。STRIDE 表の Spoofing/Elevation of Privilege 行とも整合。黙殺ではない。
- **入力検証・SQLi・XSS は React + Spring Boot + H2 構成で具体的に手当て済み**: NFR-SEC.3（サーバ側境界検証・必須/文字数チェック）、NFR-SEC.4（Spring Data JPA パラメータバインディングで文字列連結禁止 → SQLi 構造排除）、NFR-SEC.6（React デフォルトエスケープ依拠・`dangerouslySetInnerHTML` 禁止・FR2.5 の改行保持は生HTML描画せず CSS/改行変換）。スタックに即した具体性があり、tech-stack-decisions.md（Spring Data JPA / React）と矛盾しない。
- **上流を超える誇張（assumption の要件化）は見られない**: 文字数上限はいずれも「暫定」と明記し確定を OQ5 に委譲、TLS/CORS/将来認証は OQ1 等へ退避。requirements.md の FR2.6/OQ5 の「詳細は Functional Design/実装時に確定」方針を踏襲しており、未確定事項を確定要件へ格上げしていない。
- **ローカル前提下でも効くギャップが押さえられている**: CORS（NFR-SEC.10, ワイルドカード全許可回避）、秘密情報の非ハードコード（NFR-SEC.7）、ログ衛生（NFR-SEC.8, observability NFR7-OBS.3 と相互参照整合）を網羅。

### Findings

| ID | Severity | Finding | Recommendation |
|---|---|---|---|
| R-01 | Minor | Sources の `[Q3-prior]`（投稿者名は自己申告、A2）が誤ラベル。本ステージ `nfr-requirements-questions.md` の Q3 は性能目標に関する質問であり、投稿者名の自己申告は上流 requirements.md の Q2-F / A2 が出所。参照の追跡性を損なう軽微な不整合。 | 出典タグを `[A2]`（または `[Q2-F]`）へ修正し、本ステージ Q3 との混同を避ける。本文の方針自体は正しく維持。 |
| R-02 | Minor | `traceability.json` が security の派生ID群を十分に反映していない。NFR-SEC.9 のみが NFR2 の target に現れるが、NFR-SEC.1〜.8 および .10 はどの coverage 行の target にも現れない。これらは NFR{n} ではなく制約 C1/C2 と construction-phase ガードレール由来のため厳密な欠陥ではないが、セキュリティ派生要件の追跡経路が暗黙的。 | security 派生要件の由来（C1/C2・construction ガードレール）をトレーサビリティ上で明示する（例: 注記追加、または該当する NFR の target への列挙）か、security-requirements.md 冒頭に「NFR-SEC.* は NFR{n} 継承ではなく C1/C2 由来」と1行補足する。 |
| R-03 | Minor | NFR-SEC.10（CORS）の出典タグが `[Q2]`。本ステージ Q2 はビルドツール/フロント・バック分離（別ポート）に関する質問であり、CORS 要件を直接確定したものではない。別ポート構成が CORS 必要性の根拠になる点は妥当だが、タグが要件確定の直接出典のように読める。 | タグを `[Q2 由来の別ポート構成][construction-phase]` 等に補足し、CORS 自体はガードレール由来の派生要件であることを明示（任意改善）。 |
| R-04 | Minor（任意提案） | ブロッキングなし。no-auth ローカル前提下でも、全操作が無差別許可（NFR-SEC.2）であるため「社内ネットワーク外に露出した場合の即時リスク」が NFR-SEC.9 で OQ1 任せになっている。現スコープでは受容可だが、README 明記（NFR-SEC.1 で既に言及）に「公開前チェックリスト（TLS・認証・CORS 再評価）」として OQ1 を紐付けると運用移行時の取りこぼしを防げる。 | 必須ではない。将来運用移行時の安全網として OQ1 に公開前チェック項目を明記しておくと良い。 |

### Summary

no-auth/no-authz 前提は明示的スコープ判断として適切に扱われ、入力検証・SQLi・XSS・秘密情報・ログ衛生・CORS がスタックに即して現実的に網羅され、上流エビデンスを超える誇張や成果物間の矛盾も検出されなかった。残る指摘は出典タグの軽微な誤ラベル（R-01/R-03）とトレーサビリティの明示性（R-02）に留まり、いずれも READY を妨げない。
