# はじめに読むガイド（ONBOARDING）

このプロジェクトを **初めて見る人** が、何から読めばいいか迷わないための道案内です。目的別に「どの資料を・どの順で」確認すればよいかをまとめています。

> プロジェクト概要: 社内お知らせ掲示板。React（フロント）+ Spring Boot + H2（バックエンド）の最小構成 Web アプリ。学習・お試し・ローカル実行・単一利用者が前提。開発手法として **AI-DLC** を使用。

## この資料の内容（アジェンダ）

初めての人が「どの資料を・どの順で読むか」を案内する道しるべです。各セクションの要点:

- **5分で全体像をつかむ** — まず読む3点（README → RUNBOOK → AIDLC-GUIDE）
- **目的別・読む順ガイド** — 先頭に**ディレクトリ早見表**（どのフォルダが各目的の起点か）、続いて目的別の読む順（A:動かす / B:仕様 / C:コード / D:品質検証 / E:AI-DLC手法 / F:ルール）。各資料の正確なパスと「分かること」付き
- **最初の一歩（おすすめ）** — 推奨の4ステップ

---

## 5分で全体像をつかむ

1. **`README.md`** — アプリが何者か、構成、API 仕様、セキュリティ前提。まずこれ。
2. **`RUNBOOK.md`** — とにかく動かしたい人向け。ビルド・起動・停止の手順。
3. **`docs/AIDLC-GUIDE.md`** — この開発がどう進められたか（AI-DLC の流れ）。

この3つを順に見れば、「何を・どう動かし・どう作ったか」が掴めます。

---

## 目的別・読む順ガイド

まず全体の地図（どのフォルダが各目的の起点か）を示し、その後に目的別（A〜F）の読む順を並べます。

### ディレクトリ早見表

```
kiro_sample/
├── README.md               アプリ概要・API・セキュリティ前提        ← B の起点
├── RUNBOOK.md              実行手順（ビルド/起動/テスト/停止）        ← A の起点
├── ONBOARDING.md           このファイル（読む順ガイド）
├── docs/
│   └── AIDLC-GUIDE.md      AI-DLC の使い方ガイド                     ← E の起点
├── AGENTS.md               AI-DLC 全体説明
├── backend/                Spring Boot + H2（REST API, 8080）        ← C
├── frontend/               React + Vite + TS（5173）                 ← C
└── aidlc/                  AI-DLC の成果物・ルール・監査ログ          ← B/D/E/F
    └── spaces/default/
        ├── memory/         方針・ルール（org/team/project/phases）    ← F
        └── intents/261006-greenfield-web-app/
            ├── aidlc-state.md   進捗・ステージ状況
            ├── ideation/ inception/ construction/ operation/  各フェーズ成果物
            └── audit/           監査ログ
```

### A. まず動かしたい（開発環境セットアップ）

| 順 | 資料 | 分かること |
|----|------|-----------|
| 1 | `RUNBOOK.md` §1〜4 | 前提ツール、クイックスタート（backend 8080 / frontend 5173）、動作確認 |
| 2 | `README.md` 起動手順 | 補足の起動情報、H2 コンソール |
| 3 | `RUNBOOK.md` §8 | うまく動かないときのトラブルシューティング |

最短: ターミナル2つで `cd backend; ./mvnw.cmd spring-boot:run` と `cd frontend; npm install; npm run dev` → http://localhost:5173

### B. 何を作ったのか知りたい（仕様・要件）

| 順 | 資料 | 分かること |
|----|------|-----------|
| 1 | `README.md` | 機能概要・API・セキュリティ前提 |
| 2 | `aidlc/.../inception/requirements-analysis/requirements.md` | 確定した機能要件（FR）・非機能要件（NFR） |
| 3 | `aidlc/.../ideation/rough-mockups/`（wireframes / user-flow） | 画面イメージと操作フロー |
| 4 | `aidlc/.../inception/refined-mockups/` | 精緻化したモックとアクセシビリティ観点 |

（`aidlc/.../` は `aidlc/spaces/default/intents/261006-greenfield-web-app/` の略）

### C. コードを読みたい（実装）

| 順 | 対象 | 内容 |
|----|------|------|
| 1 | `aidlc/.../construction/code-generation/code-summary.md` | 実装の全体像（作成ファイルと主要な設計判断の要約）。コードを読む前の地図 |
| 2 | `backend/src/main/java/.../web/` | REST コントローラ・DTO・例外ハンドラ（プレゼンテーション層） |
| 3 | `backend/src/main/java/.../service/` | CRUD・検証・既定値補完（ビジネスロジック層） |
| 4 | `backend/src/main/java/.../domain/` `repository/` | エンティティ・永続化（データアクセス層） |
| 5 | `frontend/src/` | React コンポーネント・API クライアント・フック |

バックエンドは web / service / repository の3層分離。まず `code-summary.md` を読むと迷いません。

### D. 品質・検証を知りたい（テスト・性能）

| 資料 | 内容 |
|------|------|
| `aidlc/.../construction/build-and-test/test-results.md` | バックエンド/フロントのテスト実測結果（全グリーン） |
| `aidlc/.../construction/build-and-test/build-instructions.md` | ビルド詳細 |
| `aidlc/.../operation/performance-validation/test-results.md` | 性能実測（一覧17ms等、努力目標達成）＋起動バグ修正の記録 |
| `aidlc/.../operation/observability-setup/` | ログ（観測性）の方針 |

### E. 開発手法（AI-DLC）を理解したい

| 順 | 資料 | 分かること |
|----|------|-----------|
| 1 | `docs/AIDLC-GUIDE.md` | AI-DLC の始め方・5フェーズ・成果物の所在・実例 |
| 2 | `AGENTS.md` | AI-DLC の全体説明とファイル配置（ハーネス非依存） |
| 3 | `.kiro/steering/aidlc-onboarding.md` | Kiro IDE 固有のオンボーディング（コマンド・前提） |
| 4 | `aidlc/.../aidlc-state.md` | 現在の進捗・各ステージの完了状況（状態ファイル） |
| 5 | `aidlc/.../audit/*.md` | 決定と行動の時系列（監査ログ） |

### F. チーム/プロジェクトのルールを知りたい

| 資料 | 内容 |
|------|------|
| `aidlc/spaces/default/memory/project.md` | このプロジェクト固有のルール・学習（進め方の合意、性能は努力目標 等） |
| `aidlc/spaces/default/memory/team.md` | チームの合意（トランクベース開発、テスト方針、コードスタイル 等） |
| `aidlc/spaces/default/memory/org.md` | フレームワーク既定＋組織ルール |

ルールは `org → team → project` の順に重ねて適用されます（狭い層が特殊化を追加）。

---

## 最初の一歩（おすすめ）

1. `README.md` を読む（5分）
2. `RUNBOOK.md` に従ってローカルで起動してみる（10分）— サンプルお知らせが13件入っています
3. `docs/AIDLC-GUIDE.md` で「どう作られたか」を把握（10分）
4. 興味に応じて上の目的別ガイド（B〜F）へ

困ったら `RUNBOOK.md` §8 のトラブルシューティング、AI-DLC の状態確認は Kiro IDE チャットで `/aidlc --status`。
