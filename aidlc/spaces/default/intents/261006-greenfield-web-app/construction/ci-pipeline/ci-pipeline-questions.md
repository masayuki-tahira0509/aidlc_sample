# CI Pipeline — Clarifying Questions

CI（継続的インテグレーション）パイプラインを定義するための質問です。本プロジェクトはチーム方針で「ローカル実行のみ、本格的なデプロイパイプラインはスコープ外」と確定しています。したがって CI は「ビルドとテストを自動実行する軽量な構成」までを対象とし、本番デプロイは扱いません（deployment-pipeline はスキップ）。Construction フェーズのため質問は最小限です。各 `[Answer]:` に回答してください。

---

## Q1. CI の対象範囲

本プロジェクトでの CI の位置づけをどうしますか。チーム方針（ローカル実行のみ・デプロイはスコープ外）を踏まえた選択です。

- A. 軽量CIを用意する（push/PR 時にビルド＋テストを自動実行するだけ。デプロイなし）。学習・将来の足がかりとして設定ファイルを作る
- B. CI設定ファイルは作らず、ローカルで手動実行する手順の明文化のみ（quality-gates.md に「ローカルでこれを通すこと」を記載）
- C. 推奨に任せる（→ A: 軽量CI。GitHub Actions で build+test を自動実行）
- X. Other (please specify)

[Answer]:

---

## Q2. CI ツール（Q1でAまたはCの場合）

軽量CIを作る場合のツールを選びます。

- A. GitHub Actions（`.github/workflows/ci.yml`。GitHub 前提、最も一般的で学習向け）
- B. GitLab CI（`.gitlab-ci.yml`）
- C. 推奨に任せる（→ A: GitHub Actions）
- X. Other (please specify)

[Answer]:

---

## Q3. マージ前の品質ゲート（quality-gates.md に明記する合否基準）

CI またはローカルで「これを通さないとマージしない」とする基準を選びます（複数選択可、select all that apply）。

- A. バックエンド単体/スライステストが全件グリーン（`mvnw test`）
- B. フロントエンド単体テストが全件グリーン（`npm run test`）
- C. フロントエンドのビルドが成功（`npm run build` / 型チェック）
- D. ESLint がエラーなし（フロント）
- X. Other (please specify)

[Answer]:

---

## Assumptions & Open Questions

- [assumption] 本番デプロイ・成果物リポジトリ（ECR/S3 等）は本スコープ外（team-practices）。CI はビルド/テストの自動実行までに限定。
- [assumption] ブランチ戦略はトランクベース（main + 短命ブランチ、PR は任意）。CI は push と PR の両方で動く想定。 [team-practices Way of Working]
- [assumption] カバレッジフロアは課さない（`greenfield-webapp`）。品質ゲートはテスト合格・ビルド成功・lint 無エラーを基準とする。
