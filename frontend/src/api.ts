import type { Announcement, AnnouncementInput } from './types';

// バックエンド API のベースURL。環境変数で上書き可能（既定はローカルの 8080）。
const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080';
const ENDPOINT = `${API_BASE}/api/announcements`;

/** サーバのエラー本文（GlobalExceptionHandler の形式に対応）。 */
interface ServerError {
  status?: number;
  error?: string;
  message?: string;
}

/** API 呼び出しで発生したエラー。message はユーザー表示に用いる。 */
export class ApiError extends Error {
  readonly status: number;
  constructor(status: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

/** レスポンスを検査し、失敗ならサーバのメッセージを添えて ApiError を投げる。 */
async function parseResponse<T>(res: Response): Promise<T> {
  if (res.ok) {
    // 204 No Content は本文なし
    if (res.status === 204) {
      return undefined as T;
    }
    return (await res.json()) as T;
  }
  let message = `リクエストに失敗しました (HTTP ${res.status})`;
  try {
    const body = (await res.json()) as ServerError;
    if (body && typeof body.message === 'string') {
      message = body.message;
    }
  } catch {
    // 本文が JSON でない場合は既定メッセージのまま
  }
  throw new ApiError(res.status, message);
}

/** 通信レベルの失敗（サーバ未起動など）を分かりやすいメッセージに変換する。 */
async function request<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
  let res: Response;
  try {
    res = await fetch(input, init);
  } catch {
    throw new ApiError(0, 'サーバに接続できませんでした。バックエンドが起動しているか確認してください。');
  }
  return parseResponse<T>(res);
}

const jsonHeaders = { 'Content-Type': 'application/json' };

export const announcementApi = {
  /** 一覧取得（サーバ側で投稿日時の降順） [FR1.2]。 */
  list(): Promise<Announcement[]> {
    return request<Announcement[]>(ENDPOINT);
  },

  /** 新規登録 [FR2.1]。 */
  create(input: AnnouncementInput): Promise<Announcement> {
    return request<Announcement>(ENDPOINT, {
      method: 'POST',
      headers: jsonHeaders,
      body: JSON.stringify(input),
    });
  },

  /** 編集 [FR3.1]。 */
  update(id: number, input: AnnouncementInput): Promise<Announcement> {
    return request<Announcement>(`${ENDPOINT}/${id}`, {
      method: 'PUT',
      headers: jsonHeaders,
      body: JSON.stringify(input),
    });
  },

  /** 削除 [FR4.1]。 */
  remove(id: number): Promise<void> {
    return request<void>(`${ENDPOINT}/${id}`, { method: 'DELETE' });
  },
};
