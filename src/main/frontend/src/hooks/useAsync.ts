import { useCallback, useEffect, useMemo, useRef, useState } from 'react';

export type AsyncStatus = 'idle' | 'pending' | 'success' | 'error';

export type UseAsyncOptions<TArgs extends any[] = any[]> = {
  auto?: boolean; // auto-run on mount/dep change
  deps?: any[]; // dependencies to trigger auto run
  onSuccess?: (data: unknown, ...args: TArgs) => void;
  onError?: (err: unknown, ...args: TArgs) => void;
  immediate?: boolean; // alias for auto
};

export function useAsync<TData = unknown, TArgs extends any[] = any[]>(
  fn: (...args: TArgs) => Promise<TData>,
  options: UseAsyncOptions<TArgs> = {},
) {
  const { auto, immediate, deps = [], onSuccess, onError } = options;

  const mountedRef = useRef(true);
  const [status, setStatus] = useState<AsyncStatus>('idle');
  const [data, setData] = useState<TData | null>(null);
  const [error, setError] = useState<unknown>(null);

  useEffect(() => {
    mountedRef.current = true;
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const run = useCallback(
    async (...args: TArgs) => {
      setStatus('pending');
      setError(null);
      try {
        const result = await fn(...args);
        if (!mountedRef.current) return result;
        setData(result);
        setStatus('success');
        onSuccess?.(result, ...args);
        return result;
      } catch (err) {
        if (!mountedRef.current) throw err;
        setError(err);
        setStatus('error');
        onError?.(err, ...args);
        throw err;
      }
    },
    [fn, onSuccess, onError],
  );

  const reset = useCallback(() => {
    setStatus('idle');
    setData(null);
    setError(null);
  }, []);

  // Auto execute
  const shouldAuto = useMemo(() => (immediate ?? auto) === true, [immediate, auto]);
  useEffect(() => {
    if (shouldAuto) {
      // @ts-expect-error allow calling without args in auto mode
      run();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [shouldAuto, ...deps]);

  return { status, loading: status === 'pending', data, error, run, reset, setData, setError };
}

export default useAsync;
