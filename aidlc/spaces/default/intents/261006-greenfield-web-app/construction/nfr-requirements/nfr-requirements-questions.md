# NFR Requirements — Clarifying Questions

社内お知らせ掲示板アプリの非機能要件と技術スタックを確定するための質問です。要件定義（requirements.md）とチームプラクティスで既に決まっている内容（React + Java/Spring Boot、ローカル実行、認証なし、ファイルベース永続化など）は再質問しません。Construction フェーズのため質問は最小限に絞っています。各 `[Answer]:` に回答してください。

---

## Q1. 永続化DBの具体的な選択

NFR1 でファイルベースのDB永続化（例: SQLite / H2 ファイルモード）が要件です。Spring Boot と組み合わせる具体的なDBをどれにしますか。tech-stack-decisions.md に記録します。

- A. H2 Database（ファイルモード）— Java/Spring Boot と親和性が高く、依存が軽い。お試し・学習向け
- B. SQLite — 単一ファイルで完結。汎用的だが Spring Data JPA では追加設定がやや必要
- C. どちらでもよい（推奨に任せる → A: H2 を採用）
- X. Other (please specify)

[Answer]: C

---

## Q2. ビルドツールとフロント/バックの構成

バックエンド（Java）のビルドツールと、フロント/バックの配置方針を確定します。tech-stack-decisions.md に記録します。

- A. バックエンド=Maven、フロント/バックは別ディレクトリ（frontend/ と backend/）で分離
- B. バックエンド=Gradle、フロント/バックは別ディレクトリで分離
- C. 推奨に任せる（→ A: Maven + 分離構成。NFR7 のフロント/バック分離方針に合致）
- X. Other (please specify)

[Answer]: A

---

## Q3. ローカル実行時の性能目標の厳密さ

ローカル・単一利用者・お試し用途のため、性能は「体感で快適」程度で十分と想定しています。performance-requirements.md にどの水準で目標を書きますか。

- A. 緩やかな目安のみ（例: 一覧表示 1秒以内、保存 500ms 以内を努力目標。厳密な計測は求めない）
- B. 計測可能な目標を明記（p95 等の percentile 付きで記述し、Build and Test で確認）
- C. 性能目標は設けない（お試し段階のため）
- X. Other (please specify)

[Answer]: A （学習目的のため厳密な基準値は定めず、努力目標として柔軟に設定する）

---

## Assumptions & Open Questions

- [assumption] 認証なし（C1/C2）のため、セキュリティ要件は入力バリデーション・XSS対策・ローカル前提の最小構成とする。
- [assumption] 可用性・DR・分散トレーシング等のエンタープライズ級の非機能はローカル・お試し用途のためスコープ外（必要最小限の観測性＝ログのみ）。
- [assumption] スケーラビリティは NFR9/OQ4 の方針（全件表示、将来ページング後付け可能な構造）を踏襲する。

## Consolidated Summary Confirmation

生成前の最終確認。以下の判断で非機能要件・技術スタックの各成果物を作成します。

- Q1: 永続化DB = H2 Database（ファイルモード）を採用
- Q2: ビルドツール = Maven、フロント/バック分離構成（frontend/ と backend/、Maven Wrapper 同梱）
- Q3: 性能目標 = 緩やかな努力目標のみ（例: 一覧表示1秒以内・保存500ms以内）。厳密な基準値は定めない（学習目的）
- セキュリティ: 認証なし（C1/C2）前提。入力バリデーション・XSS対策・ローカル前提の最小構成
- 可用性/DR/分散トレーシング等のエンタープライズ級非機能はスコープ外。観測性はログ中心の最小構成
- スケーラビリティ: NFR9/OQ4 方針（全件表示、将来ページング後付け可能な構造）

- Looks correct
- Request changes

[Answer]: Looks correct
