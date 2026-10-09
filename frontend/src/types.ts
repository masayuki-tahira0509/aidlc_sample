// お知らせのドメイン型定義

/** カテゴリの暫定許可値（確定は OQ2） [A3]。 */
export const CATEGORIES = ['一般', '重要', '業務連絡'] as const;
export type Category = (typeof CATEGORIES)[number];

/** サーバから取得するお知らせ。 */
export interface Announcement {
  id: number;
  title: string;
  body: string;
  author: string;
  category: string;
  createdAt: string; // ISO-8601 文字列
}

/** 作成/編集フォームの入力値。 */
export interface AnnouncementInput {
  title: string;
  body: string;
  author: string;
  category: string;
}

/** 一覧の読み込み状態。 */
export type ListStatus = 'loading' | 'ready' | 'empty' | 'error';
