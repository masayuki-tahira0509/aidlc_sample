import { useEffect } from 'react';

// 保存/削除成功の一時通知 [interaction-spec: Toast]
// aria-live="polite" で割り込まず読み上げる [WCAG 4.1.3]。

interface ToastProps {
  message: string;
  duration?: number;
  onDismiss: () => void;
}

export function Toast({ message, duration = 3000, onDismiss }: ToastProps) {
  useEffect(() => {
    const timer = window.setTimeout(onDismiss, duration);
    return () => window.clearTimeout(timer);
  }, [message, duration, onDismiss]);

  return (
    <div className="toast" role="status" aria-live="polite" data-testid="toast">
      {message}
    </div>
  );
}
