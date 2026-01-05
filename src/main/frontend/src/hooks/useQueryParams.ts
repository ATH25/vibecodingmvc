import { useCallback, useMemo } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

export type QueryInit = Record<string, string | number | boolean | undefined | null>;

export type UpdateMode = 'push' | 'replace';

function toSearchParams(init: QueryInit, base?: URLSearchParams) {
  const params = new URLSearchParams(base);
  Object.entries(init).forEach(([k, v]) => {
    if (v === undefined || v === null || v === '') {
      params.delete(k);
    } else {
      params.set(k, String(v));
    }
  });
  return params;
}

export function useQueryParams<T extends QueryInit = QueryInit>() {
  const location = useLocation();
  const navigate = useNavigate();

  const params = useMemo(() => new URLSearchParams(location.search), [location.search]);

  const all = useMemo(() => {
    const obj: Record<string, string> = {};
    params.forEach((v, k) => {
      obj[k] = v;
    });
    return obj as T;
  }, [params]);

  const setParams = useCallback(
    (patch: QueryInit | ((current: T) => QueryInit), mode: UpdateMode = 'push') => {
      const current = Object.fromEntries(params.entries()) as T;
      const nextInit = typeof patch === 'function' ? patch(current) : patch;
      const next = toSearchParams(nextInit, params);
      const search = next.toString();
      const url = `${location.pathname}${search ? `?${search}` : ''}${location.hash ?? ''}`;
      if (mode === 'replace') navigate(url, { replace: true });
      else navigate(url);
    },
    [params, location.pathname, location.hash, navigate],
  );

  const get = useCallback((key: string) => params.get(key), [params]);

  return { params: all, get, setParams };
}

export default useQueryParams;
