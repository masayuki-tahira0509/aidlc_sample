import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AnnouncementFormModal } from './AnnouncementFormModal';

// フォーム送信のハッピーパスと必須検証（最小テスト）

describe('AnnouncementFormModal', () => {
  it('submits valid input', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    render(<AnnouncementFormModal mode="create" onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.type(screen.getByTestId('title-input'), 'タイトル');
    await user.type(screen.getByTestId('body-input'), '本文です');
    await user.selectOptions(screen.getByTestId('category-select'), '一般');
    await user.click(screen.getByTestId('form-submit-button'));

    await waitFor(() => expect(onSubmit).toHaveBeenCalledTimes(1));
    expect(onSubmit).toHaveBeenCalledWith({
      title: 'タイトル',
      body: '本文です',
      author: '',
      category: '一般',
    });
  });

  it('shows validation errors and does not submit when required fields are missing', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();
    render(<AnnouncementFormModal mode="create" onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.click(screen.getByTestId('form-submit-button'));

    expect(screen.getByTestId('title-error')).toBeInTheDocument();
    expect(screen.getByTestId('body-error')).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });
});
