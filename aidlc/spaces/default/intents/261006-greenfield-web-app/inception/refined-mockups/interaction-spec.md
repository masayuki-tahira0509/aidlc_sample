# Interaction Specification — 社内お知らせ掲示板

モックアップの主要コンポーネントについて、状態・入力・レスポンシブ・アクセシビリティを `component-spec-template.md` の形式で定義する。デスクトップ専用単一レイアウトのため、レスポンシブ欄は単一ブレークポイント前提で記す。 [Q6=A]

---

## AnnouncementList（お知らせ一覧）

| Field | Value |
|---|---|
| Component | AnnouncementList |
| Description | 登録済みお知らせを投稿日時の降順で表示する一覧コンテナ | [FR1.1][FR1.2] |
| Category | display |

### States

| State | Description | Trigger |
|---|---|---|
| default | 1件以上のお知らせを行で表示 | 一覧データ取得成功 |
| loading | スケルトン行を表示 | API取得中 [Q3=A] |
| empty | 「まだお知らせはありません」を表示 | 取得結果0件 [FR1.1] |
| error | 読込失敗メッセージ + 再読み込みボタン | API取得失敗 [Q3=B] |

### Props / Inputs

| Prop | Type | Required | Default | Description |
|---|---|---|---|---|
| announcements | object[] | yes | [] | 表示するお知らせ配列（降順ソート済み） |
| status | string | yes | "loading" | loading \| ready \| empty \| error |
| onCreate | function | yes | — | 新規作成ボタン押下時のハンドラ |
| onEdit | function | yes | — | 行の編集押下時（id を渡す） |
| onDelete | function | yes | — | 行の削除押下時（id を渡す） |
| onRetry | function | no | — | エラー時の再読み込みハンドラ |

### Responsive Behaviour

| Breakpoint | Behaviour |
|---|---|
| desktop (単一レイアウト) | 全カラム（カテゴリ/タイトル/投稿者/日時/操作）を横並び表示。密度重視のコンパクト行高 [Q5=C][Q6=A] |

### Accessibility

| Requirement | Implementation |
|---|---|
| ARIA role | リスト構造は `<table>` もしくは `role="list"`。ヘッダー行はセマンティックに表現 |
| Keyboard interaction | 各行の操作ボタンへ Tab で到達、Enter/Space で起動。論理的なタブ順 [WCAG 2.1.1] |
| Label / aria-label | 操作アイコンに `aria-label`（「編集」「削除」）。アイコン単体でも意味が伝わる |
| Contrast ratio | 本文 4.5:1、カテゴリバッジ・アイコン等 UI 要素 3:1 以上 [WCAG 1.4.3][1.4.11] |
| Screen reader | loading 時 `aria-busy="true"`、error は `role="alert"` で通知 [WCAG 4.1.3] |
| Focus management | 行削除後はフォーカスを一覧先頭または隣接行へ移す |

### Usage Example

```
<AnnouncementList
  announcements={items}
  status="ready"
  onCreate={openCreateModal}
  onEdit={openEditModal}
  onDelete={openDeleteConfirm}
/>
```

---

## CategoryBadge（カテゴリバッジ）

| Field | Value |
|---|---|
| Component | CategoryBadge |
| Description | お知らせ種別を一覧上で控えめな色＋ラベルで示す | [FR5.1][Q4=A] |
| Category | display |

### States

| State | Description | Trigger |
|---|---|---|
| default | ラベル + 控えめな背景色のバッジ | 常時 |

### Props / Inputs

| Prop | Type | Required | Default | Description |
|---|---|---|---|---|
| category | string | yes | — | カテゴリ値（暫定: 一般 \| 重要 \| 業務連絡） [A3] |

### Responsive Behaviour

| Breakpoint | Behaviour |
|---|---|
| desktop (単一レイアウト) | ラベルを常時テキスト表示。省略しない [Q6=A] |

### Accessibility

| Requirement | Implementation |
|---|---|
| ARIA role | 装飾ではなく情報。テキストラベルを必ず含む |
| Contrast ratio | 背景色と文字で 4.5:1 以上を確保 [WCAG 1.4.3] |
| Screen reader | 色ではなくラベル文字で種別が読み上げられる（色のみ依存禁止） [WCAG 1.4.1] |

### Usage Example

```
<CategoryBadge category="重要" />
```

---

## AnnouncementFormModal（作成/編集モーダル）

| Field | Value |
|---|---|
| Component | AnnouncementFormModal |
| Description | 新規作成と編集を兼ねる入力モーダル | [Q1=B][FR2][FR3] |
| Category | input |

### States

| State | Description | Trigger |
|---|---|---|
| default | 空フォーム（作成）/ プリフィル（編集） | モーダルを開く |
| validation-error | 必須未入力・上限超過をフィールド直下に表示 | 保存時の検証失敗 [Q3=D][FR2.6] |
| submitting | 保存ボタンを「保存中…」+スピナー、二重送信防止 | 保存処理中 [Q3=A] |
| error | 保存API失敗。モーダル上部にエラー表示 | 保存リクエスト失敗 [Q3=B] |

### Props / Inputs

| Prop | Type | Required | Default | Description |
|---|---|---|---|---|
| mode | string | yes | "create" | create \| edit |
| initialValue | object | no | null | 編集時のプリフィル値 [FR3.1] |
| onSubmit | function | yes | — | 保存ハンドラ（検証後に呼ぶ） |
| onCancel | function | yes | — | キャンセル/×/Escape のハンドラ |
| categories | string[] | yes | — | カテゴリ選択肢（暫定3値） [A3] |

### Responsive Behaviour

| Breakpoint | Behaviour |
|---|---|
| desktop (単一レイアウト) | 中央オーバーレイ、固定幅のモーダル。背景を暗転 [Q6=A] |

### Accessibility

| Requirement | Implementation |
|---|---|
| ARIA role | `role="dialog"` + `aria-modal="true"` + `aria-labelledby`（モーダルタイトル） |
| Keyboard interaction | Escape で閉じる、Tab はモーダル内にフォーカストラップ、開く時は先頭入力へフォーカス [WCAG 2.1.2] |
| Label / aria-label | 各入力に `<label>` を `for`/`id` で関連付け。必須は `aria-required="true"` |
| Contrast ratio | 入力枠・プレースホルダ・エラー文で基準値を満たす [WCAG 1.4.3][1.4.11] |
| Screen reader | エラーは `aria-invalid` + `aria-describedby` で各フィールドに関連付け [WCAG 3.3.1] |
| Focus management | 閉じたら元の起点（新規作成ボタン/編集アイコン）へフォーカスを戻す |

### Usage Example

```
<AnnouncementFormModal
  mode="edit"
  initialValue={selected}
  categories={["一般", "重要", "業務連絡"]}
  onSubmit={save}
  onCancel={close}
/>
```

---

## DeleteConfirmModal（削除確認モーダル）

| Field | Value |
|---|---|
| Component | DeleteConfirmModal |
| Description | 削除前に対象を明示して確認を取る | [Q2=A][FR4.2] |
| Category | feedback |

### States

| State | Description | Trigger |
|---|---|---|
| default | 対象タイトルと取り消し不可の注意を表示 | 削除アイコン押下 |
| deleting | 「削除する」を無効化しスピナー表示 | 削除処理中 [Q3=A] |

### Props / Inputs

| Prop | Type | Required | Default | Description |
|---|---|---|---|---|
| title | string | yes | — | 削除対象お知らせのタイトル |
| onConfirm | function | yes | — | 「削除する」ハンドラ |
| onCancel | function | yes | — | 「キャンセル」/Escape ハンドラ |

### Responsive Behaviour

| Breakpoint | Behaviour |
|---|---|
| desktop (単一レイアウト) | 中央オーバーレイ、固定幅 [Q6=A] |

### Accessibility

| Requirement | Implementation |
|---|---|
| ARIA role | `role="alertdialog"` + `aria-labelledby` + `aria-describedby` |
| Keyboard interaction | Escape でキャンセル、フォーカストラップ、初期フォーカスは「キャンセル」 [WCAG 3.3.4] |
| Contrast ratio | ボタン・本文で基準値を満たす [WCAG 1.4.3] |
| Screen reader | 開いた瞬間に対象と注意文を読み上げ |
| Focus management | 閉じたら削除アイコン（または一覧先頭）へフォーカスを戻す |

### Usage Example

```
<DeleteConfirmModal
  title="システムメンテナンスのお知らせ"
  onConfirm={doDelete}
  onCancel={close}
/>
```

---

## Toast（保存/削除フィードバック）

| Field | Value |
|---|---|
| Component | Toast |
| Description | 保存・削除成功を一時的に通知 | [Q3=C] |
| Category | feedback |

### States

| State | Description | Trigger |
|---|---|---|
| visible | メッセージを数秒表示後に自動消滅 | 保存/削除成功 |

### Props / Inputs

| Prop | Type | Required | Default | Description |
|---|---|---|---|---|
| message | string | yes | — | 表示文（例「お知らせを保存しました」） |
| duration | number | no | 3000 | 表示時間(ms) |

### Responsive Behaviour

| Breakpoint | Behaviour |
|---|---|
| desktop (単一レイアウト) | 画面上部に重ねて表示 [Q6=A] |

### Accessibility

| Requirement | Implementation |
|---|---|
| ARIA role | `aria-live="polite"`（割り込まず通知） [WCAG 4.1.3] |
| Keyboard interaction | 操作不要。キーボードフォーカスは奪わない |
| Contrast ratio | 背景と文字で 4.5:1 以上 [WCAG 1.4.3] |
| Screen reader | メッセージ変化が読み上げられる |

### Usage Example

```
<Toast message="お知らせを保存しました" />
```

## Assumptions & Open Questions

- [assumption] カテゴリ選択肢の確定値は Functional Design / 実装時（OQ2）。
- [assumption] 文字数上限値は実装時に確定（OQ5）。本仕様では検証を「必須＋上限」の枠組みとして定義し、具体値は未固定。
