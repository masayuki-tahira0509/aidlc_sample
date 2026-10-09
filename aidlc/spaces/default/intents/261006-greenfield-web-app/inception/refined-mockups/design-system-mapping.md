# Design System Mapping — 社内お知らせ掲示板

本プロジェクトは Greenfield で既存のデザインシステムは存在しない。情報密度重視・ニュートラルで軽量な設計方針（Q5=C）に沿い、既存の重量級UIフレームワークは導入せず、最小限のデザイントークンと素のコンポーネントで構成する。React + ESLint/Prettier のチームプラクティスに整合させる。 [Q5=C][team-practices]

## 前提

- 既存デザインシステム: なし（Greenfield）。独自に軽量トークンを定義。 [requirements]
- UIライブラリ: 必須としない。素のReactコンポーネント + CSS（CSS Modules などプロジェクト標準）を基本とする。学習目的に沿い過剰な抽象化は避ける。 [NFR8]
- フレームワーク: React（フロント）、Spring Boot（バック）。本書はフロントの見た目のみを対象。 [NFR3]

## デザイントークン

情報密度重視のため余白は控えめ、フォントは小さめ基準。すべてのトークンはコントラスト基準（WCAG 2.1 AA）を満たす値を採用する。 [Q5=C][Q7=A]

### カラー

| トークン | 用途 | 例（暫定値） | コントラスト確認 |
|---------|------|------------|----------------|
| color-bg | 画面背景 | #FFFFFF | 基準 |
| color-surface | カード/モーダル面 | #F7F8FA | — |
| color-text | 本文テキスト | #1F2328 | 対 bg 4.5:1 以上 [1.4.3] |
| color-text-muted | 補助テキスト（日時等） | #5A6168 | 対 bg 4.5:1 以上 |
| color-accent | 主要アクション（保存/新規作成） | #2563EB | 対白文字 4.5:1 以上 |
| color-danger | 削除・エラー | #D13438 | 対白文字 4.5:1 以上 |
| color-border | 区切り線・入力枠 | #D0D5DD | UI 3:1 以上 [1.4.11] |
| color-focus | フォーカスリング | #2563EB | 2px、対背景 3:1 以上 [2.4.7] |

### カテゴリ色（控えめ・色のみ依存しない） [Q4=A][1.4.1]

| カテゴリ（暫定） | 背景 | 文字 | 併用する非色手がかり |
|-----------------|------|------|--------------------|
| 一般 | #EEF1F4 | #1F2328 | ラベル文字「一般」+ ○ 形状 |
| 重要 | #FDECEC | #B42318 | ラベル文字「重要」+ ⬤ 塗りつぶし形状 |
| 業務連絡 | #EAF2FB | #1E4E8C | ラベル文字「業務連絡」+ ◐ 形状 |

区分値は暫定。確定は Functional Design / 実装時（OQ2）。 [open-question]

### タイポグラフィ

| トークン | 値（暫定） | 用途 |
|---------|-----------|------|
| font-base | 14px | 本文・一覧行（密度重視） [Q5=C] |
| font-sm | 12px | 補助情報（投稿日時） |
| font-title | 16px bold | 画面/モーダルタイトル |
| line-height | 1.5 | 可読性確保 |

### スペーシング・形状

| トークン | 値（暫定） | 用途 |
|---------|-----------|------|
| space-xs / sm / md | 4 / 8 / 16px | 密度重視で小さめ基準 [Q5=C] |
| radius | 4px | 角丸（控えめ） |
| row-height | 40px | 一覧行の高さ（コンパクト） |

## コンポーネント対応表

| モックアップ要素 | コンポーネント | 実装方針 |
|-----------------|--------------|---------|
| お知らせ一覧 | AnnouncementList | `<table>` または `role="list"`。トークンで行高・区切り線を適用 |
| カテゴリバッジ | CategoryBadge | ラベル文字 + 形状 + 控えめ背景色。色のみに依存しない |
| 作成/編集モーダル | AnnouncementFormModal | `role="dialog"` + フォーカストラップ。ネイティブ `<input>`/`<textarea>`/`<select>` を使用 |
| 削除確認モーダル | DeleteConfirmModal | `role="alertdialog"`。ネイティブ `<button>` |
| トースト | Toast | `aria-live="polite"` の軽量通知 |
| ボタン（主/副/危険） | Button | variant: primary(accent) / secondary / danger(color-danger) |
| 入力・選択 | TextField / TextArea / Select | ネイティブ要素優先（ARIA より素のHTMLを優先） [a11y] |

## 命名・コード規約の整合

- コンポーネント名: PascalCase（React 慣習・team-practices の言語イディオム）。 [team-practices]
- CSS クラス/トークン名: ケバブケース（`color-text` など）。
- ESLint + Prettier を適用し、スタイル・整形はツール任せ。独自ルールは設けない。 [team-practices][NFR5]

## 対象外

- 実画像のデザインカンプ・ブランドロゴ・アニメーション作り込み（本ステージ対象外）。
- ダークモード・テーマ切替（スコープ外。トークン化しているため将来追加は容易）。 [NFR8]

## Assumptions & Open Questions

- [assumption] 具体的なカラーコード・サイズは暫定値。実装時に微調整可（コントラスト基準は維持）。
- [assumption] UIライブラリ不採用は方針。必要になれば軽量ライブラリ導入を実装時に再検討。
- [assumption] カテゴリ区分値は Functional Design / 実装時に確定（OQ2）。
