import type { Announcement, ListStatus } from '../types';
import { formatDateTime } from '../utils/format';
import { CategoryBadge } from './CategoryBadge';

// お知らせ一覧 [FR1.1][FR1.2][interaction-spec: AnnouncementList]
// 状態: loading / ready / empty / error。

interface AnnouncementListProps {
  announcements: Announcement[];
  status: ListStatus;
  onCreate: () => void;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
  onRetry?: () => void;
}

export function AnnouncementList({
  announcements,
  status,
  onCreate,
  onEdit,
  onDelete,
  onRetry,
}: AnnouncementListProps) {
  return (
    <section className="announcement-list" data-testid="announcement-list">
      <header className="list-header">
        <h1>社内お知らせ掲示板</h1>
        <button type="button" onClick={onCreate} data-testid="create-button">
          新規作成
        </button>
      </header>

      {status === 'loading' && (
        <div className="list-loading" aria-busy="true" data-testid="list-loading">
          <p>読み込み中…</p>
        </div>
      )}

      {status === 'error' && (
        <div className="list-error" role="alert" data-testid="list-error">
          <p>お知らせの読み込みに失敗しました。</p>
          {onRetry && (
            <button type="button" onClick={onRetry} data-testid="retry-button">
              再読み込み
            </button>
          )}
        </div>
      )}

      {status === 'empty' && (
        <div className="list-empty" data-testid="list-empty">
          <p>まだお知らせはありません。</p>
        </div>
      )}

      {status === 'ready' && (
        <table className="list-table" data-testid="list-table">
          <thead>
            <tr>
              <th scope="col">カテゴリ</th>
              <th scope="col">タイトル</th>
              <th scope="col">投稿者</th>
              <th scope="col">投稿日時</th>
              <th scope="col">操作</th>
            </tr>
          </thead>
          <tbody>
            {announcements.map((a) => (
              <tr key={a.id} data-testid={`list-row-${a.id}`}>
                <td>
                  <CategoryBadge category={a.category} />
                </td>
                <td className="cell-title">{a.title}</td>
                <td>{a.author}</td>
                <td>{formatDateTime(a.createdAt)}</td>
                <td className="cell-actions">
                  <button
                    type="button"
                    aria-label={`「${a.title}」を編集`}
                    onClick={() => onEdit(a.id)}
                    data-testid={`edit-button-${a.id}`}
                  >
                    編集
                  </button>
                  <button
                    type="button"
                    aria-label={`「${a.title}」を削除`}
                    onClick={() => onDelete(a.id)}
                    data-testid={`delete-button-${a.id}`}
                  >
                    削除
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}
