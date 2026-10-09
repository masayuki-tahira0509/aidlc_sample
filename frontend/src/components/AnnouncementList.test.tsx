import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { AnnouncementList } from './AnnouncementList';
import type { Announcement } from '../types';

// 一覧表示のハッピーパス（最小テスト） [team-practices: フロントは任意・最小]

const sample: Announcement[] = [
  {
    id: 1,
    title: 'システムメンテナンスのお知らせ',
    body: '本文です',
    author: '管理者',
    category: '重要',
    createdAt: '2026-01-01T00:00:00Z',
  },
];

describe('AnnouncementList', () => {
  it('renders rows when status is ready', () => {
    render(
      <AnnouncementList
        announcements={sample}
        status="ready"
        onCreate={vi.fn()}
        onEdit={vi.fn()}
        onDelete={vi.fn()}
      />,
    );
    expect(screen.getByText('システムメンテナンスのお知らせ')).toBeInTheDocument();
    expect(screen.getByTestId('category-badge')).toHaveTextContent('重要');
  });

  it('shows empty message when status is empty', () => {
    render(
      <AnnouncementList
        announcements={[]}
        status="empty"
        onCreate={vi.fn()}
        onEdit={vi.fn()}
        onDelete={vi.fn()}
      />,
    );
    expect(screen.getByTestId('list-empty')).toBeInTheDocument();
  });
});
