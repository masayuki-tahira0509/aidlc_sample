import { useEffect, type RefObject } from 'react';

/**
 * モーダル内にフォーカスをトラップし、Escape で閉じるためのフック
 * [WCAG 2.1.2][interaction-spec]。
 *
 * @param containerRef フォーカストラップ対象のコンテナ
 * @param onEscape     Escape キー押下時のハンドラ
 * @param initialFocusRef 開いた直後にフォーカスを当てる要素（任意）
 */
export function useFocusTrap(
  containerRef: RefObject<HTMLElement>,
  onEscape: () => void,
  initialFocusRef?: RefObject<HTMLElement>,
): void {
  useEffect(() => {
    const container = containerRef.current;
    if (!container) {
      return;
    }

    // 開いた直前のフォーカス要素を記憶し、閉じたら戻す
    const previouslyFocused = document.activeElement as HTMLElement | null;

    const focusableSelector =
      'a[href], button:not([disabled]), textarea, input, select, [tabindex]:not([tabindex="-1"])';

    // 初期フォーカス
    const target = initialFocusRef?.current ?? container.querySelector<HTMLElement>(focusableSelector);
    target?.focus();

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        e.preventDefault();
        onEscape();
        return;
      }
      if (e.key !== 'Tab') {
        return;
      }
      const focusable = Array.from(
        container.querySelectorAll<HTMLElement>(focusableSelector),
      ).filter((el) => el.offsetParent !== null || el === document.activeElement);
      if (focusable.length === 0) {
        return;
      }
      const first = focusable[0];
      const last = focusable[focusable.length - 1];
      if (e.shiftKey && document.activeElement === first) {
        e.preventDefault();
        last.focus();
      } else if (!e.shiftKey && document.activeElement === last) {
        e.preventDefault();
        first.focus();
      }
    };

    container.addEventListener('keydown', handleKeyDown);
    return () => {
      container.removeEventListener('keydown', handleKeyDown);
      previouslyFocused?.focus();
    };
  }, [containerRef, onEscape, initialFocusRef]);
}
