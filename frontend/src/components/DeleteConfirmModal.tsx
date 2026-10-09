import { useRef, useState } from 'react';
import { useFocusTrap } from '../hooks/useFocusTrap';

// 削除確認モーダル [FR4.2][interaction-spec: DeleteConfirmModal]
// role="alertdialog"、初期フォーカスは「キャンセル」[WCAG 3.3.4]。

interface DeleteConfirmModalProps {
  title: string;
  onConfirm: () => Promise<void>;
  onCancel: () => void;
}

export function DeleteConfirmModal({ title, onConfirm, onCancel }: DeleteConfirmModalProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const cancelRef = useRef<HTMLButtonElement>(null);
  useFocusTrap(containerRef, onCancel, cancelRef);

  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleConfirm = async () => {
    setDeleting(true);
    setError(null);
    try {
      await onConfirm();
    } catch (err) {
      setError(err instanceof Error ? err.message : '削除に失敗しました。');
      setDeleting(false);
    }
  };

  const headingId = 'delete-modal-heading';
  const descId = 'delete-modal-desc';

  return (
    <div className="modal-overlay" data-testid="delete-modal-overlay">
      <div
        className="modal modal-delete"
        role="alertdialog"
        aria-modal="true"
        aria-labelledby={headingId}
        aria-describedby={descId}
        ref={containerRef}
        data-testid="delete-modal"
      >
        <h2 id={headingId}>お知らせの削除</h2>
        <p id={descId}>
          「{title}」を削除します。この操作は取り消せません。
        </p>

        {error && (
          <div className="modal-error" role="alert" data-testid="delete-error">
            {error}
          </div>
        )}

        <div className="modal-actions">
          <button
            type="button"
            ref={cancelRef}
            onClick={onCancel}
            disabled={deleting}
            data-testid="delete-cancel-button"
          >
            キャンセル
          </button>
          <button
            type="button"
            className="danger"
            onClick={handleConfirm}
            disabled={deleting}
            data-testid="delete-confirm-button"
          >
            {deleting ? '削除中…' : '削除する'}
          </button>
        </div>
      </div>
    </div>
  );
}
