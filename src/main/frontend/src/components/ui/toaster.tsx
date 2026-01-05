import React, { useEffect, useState } from 'react';

import { ErrorToastEventName, type ErrorToastEventDetail } from '../../lib/errors';

type Toast = {
  id: number;
  title?: string;
  description?: string;
  variant?: 'default' | 'destructive' | 'success';
};

export function Toaster() {
  const [toasts, setToasts] = useState<Toast[]>([]);

  useEffect(() => {
    let idCounter = 0;
    const addToast = (t: Omit<Toast, 'id'>) => {
      const id = ++idCounter;
      const toast: Toast = { id, ...t };
      setToasts((prev) => [...prev, toast]);
      // Auto dismiss
      window.setTimeout(() => dismiss(id), 4000);
    };

    const dismiss = (id: number) => setToasts((prev) => prev.filter((t) => t.id !== id));

    const onErrorToast = (e: Event) => {
      const detail = (e as CustomEvent<ErrorToastEventDetail>).detail;
      addToast({
        title: detail?.title ?? 'Error',
        description: detail?.description ?? 'Something went wrong',
        variant: 'destructive',
      });
    };

    window.addEventListener(ErrorToastEventName, onErrorToast as EventListener);
    return () => window.removeEventListener(ErrorToastEventName, onErrorToast as EventListener);
  }, []);

  if (!toasts.length) return null;

  return (
    <div className="fixed inset-x-0 top-2 z-50 flex flex-col items-center gap-2">
      {toasts.map((t) => (
        <div
          key={t.id}
          role="status"
          className={`w-[min(96%,560px)] rounded-md border px-4 py-3 shadow-sm ${
            t.variant === 'destructive'
              ? 'bg-destructive/10 text-destructive-foreground border-destructive'
              : 'bg-card text-card-foreground'
          }`}
        >
          {t.title && <div className="font-medium">{t.title}</div>}
          {t.description && <div className="text-sm text-muted-foreground">{t.description}</div>}
        </div>
      ))}
    </div>
  );
}
