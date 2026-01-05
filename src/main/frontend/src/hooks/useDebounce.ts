import { useEffect, useMemo, useRef, useState } from 'react';

export function useDebounce<T>(value: T, delay = 300): T {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(id);
  }, [value, delay]);
  return debounced;
}

export function useDebouncedCallback<F extends (...args: any[]) => any>(fn: F, delay = 300) {
  const timer = useRef<number | null>(null);
  const saved = useRef(fn);

  useEffect(() => {
    saved.current = fn;
  }, [fn]);

  return useMemo(
    () =>
      ((...args: Parameters<F>) => {
        if (timer.current) window.clearTimeout(timer.current);
        timer.current = window.setTimeout(() => {
          saved.current(...args);
        }, delay);
      }) as F,
    [delay],
  );
}

export default useDebounce;
