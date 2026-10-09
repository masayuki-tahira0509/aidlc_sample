import { useRef, useState, type FormEvent } from 'react';
import { CATEGORIES, type Announcement, type AnnouncementInput } from '../types';
import { useFocusTrap } from '../hooks/useFocusTrap';

// 作成/編集モーダル [FR2][FR3][interaction-spec: AnnouncementFormModal]
// 状態: default / validation-error / submitting / error。

const MAX_TITLE = 100;
const MAX_BODY = 2000;

interface AnnouncementFormModalProps {
  mode: 'create' | 'edit';
  initialValue?: Announcement | null;
  onSubmit: (input: AnnouncementInput) => Promise<void>;
  onCancel: () => void;
}

interface FieldErrors {
  title?: string;
  body?: string;
  category?: string;
}

// クライアント側検証（UX 目的。サーバ側検証を正とする [NFR-SEC.3]）。
function validate(input: AnnouncementInput): FieldErrors {
  const errors: FieldErrors = {};
  if (!input.title.trim()) {
    errors.title = 'タイトルは必須です。';
  } else if (input.title.trim().length > MAX_TITLE) {
    errors.title = `タイトルは${MAX_TITLE}文字以内で入力してください。`;
  }
  if (!input.body.trim()) {
    errors.body = '本文は必須です。';
  } else if (input.body.length > MAX_BODY) {
    errors.body = `本文は${MAX_BODY}文字以内で入力してください。`;
  }
  if (!input.category) {
    errors.category = 'カテゴリは必須です。';
  }
  return errors;
}

export function AnnouncementFormModal({
  mode,
  initialValue,
  onSubmit,
  onCancel,
}: AnnouncementFormModalProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const titleRef = useRef<HTMLInputElement>(null);
  useFocusTrap(containerRef, onCancel, titleRef);

  const [title, setTitle] = useState(initialValue?.title ?? '');
  const [body, setBody] = useState(initialValue?.body ?? '');
  const [author, setAuthor] = useState(initialValue?.author ?? '');
  const [category, setCategory] = useState(initialValue?.category ?? '');
  const [errors, setErrors] = useState<FieldErrors>({});
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const headingId = 'form-modal-heading';
  const heading = mode === 'create' ? 'お知らせを新規作成' : 'お知らせを編集';

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const input: AnnouncementInput = { title, body, author, category };
    const found = validate(input);
    setErrors(found);
    if (Object.keys(found).length > 0) {
      return;
    }
    setSubmitting(true);
    setSubmitError(null);
    try {
      await onSubmit(input);
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : '保存に失敗しました。');
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" data-testid="form-modal-overlay">
      <div
        className="modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby={headingId}
        ref={containerRef}
        data-testid="form-modal"
      >
        <h2 id={headingId}>{heading}</h2>

        {submitError && (
          <div className="modal-error" role="alert" data-testid="form-submit-error">
            {submitError}
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          <div className="field">
            <label htmlFor="title-input">
              タイトル<span aria-hidden="true"> *</span>
            </label>
            <input
              id="title-input"
              ref={titleRef}
              type="text"
              value={title}
              maxLength={MAX_TITLE}
              aria-required="true"
              aria-invalid={errors.title ? 'true' : undefined}
              aria-describedby={errors.title ? 'title-error' : undefined}
              onChange={(e) => setTitle(e.target.value)}
              data-testid="title-input"
            />
            {errors.title && (
              <p id="title-error" className="field-error" data-testid="title-error">
                {errors.title}
              </p>
            )}
          </div>

          <div className="field">
            <label htmlFor="body-input">
              本文<span aria-hidden="true"> *</span>
            </label>
            <textarea
              id="body-input"
              value={body}
              rows={6}
              maxLength={MAX_BODY}
              aria-required="true"
              aria-invalid={errors.body ? 'true' : undefined}
              aria-describedby={errors.body ? 'body-error' : undefined}
              onChange={(e) => setBody(e.target.value)}
              data-testid="body-input"
            />
            {errors.body && (
              <p id="body-error" className="field-error" data-testid="body-error">
                {errors.body}
              </p>
            )}
          </div>

          <div className="field">
            <label htmlFor="author-input">投稿者名（任意）</label>
            <input
              id="author-input"
              type="text"
              value={author}
              placeholder="未入力の場合は「名無し」"
              onChange={(e) => setAuthor(e.target.value)}
              data-testid="author-input"
            />
          </div>

          <div className="field">
            <label htmlFor="category-select">
              カテゴリ<span aria-hidden="true"> *</span>
            </label>
            <select
              id="category-select"
              value={category}
              aria-required="true"
              aria-invalid={errors.category ? 'true' : undefined}
              aria-describedby={errors.category ? 'category-error' : undefined}
              onChange={(e) => setCategory(e.target.value)}
              data-testid="category-select"
            >
              <option value="">選択してください</option>
              {CATEGORIES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
            {errors.category && (
              <p id="category-error" className="field-error" data-testid="category-error">
                {errors.category}
              </p>
            )}
          </div>

          <div className="modal-actions">
            <button
              type="button"
              onClick={onCancel}
              disabled={submitting}
              data-testid="form-cancel-button"
            >
              キャンセル
            </button>
            <button type="submit" disabled={submitting} data-testid="form-submit-button">
              {submitting ? '保存中…' : '保存'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
