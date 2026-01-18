import { useCallback, useMemo, useState } from 'react';

export type UsePaginationOptions = {
  page?: number; // 1-based page
  pageSize?: number;
  total?: number; // optional total items to compute totalPages
  minPageSize?: number;
  maxPageSize?: number;
};

export function usePagination(opts: UsePaginationOptions = {}) {
  const {
    page: initialPage = 1,
    pageSize: initialPageSize = 10,
    total,
    minPageSize = 5,
    maxPageSize = 100,
  } = opts;

  const [page, setPage] = useState(Math.max(1, initialPage));
  const [pageSize, setPageSizeState] = useState(
    Math.min(Math.max(minPageSize, initialPageSize), maxPageSize),
  );

  const pageIndex = useMemo(() => Math.max(0, page - 1), [page]);
  const totalPages = useMemo(
    () => (total != null ? Math.max(1, Math.ceil(total / pageSize)) : undefined),
    [total, pageSize],
  );

  const next = useCallback(() => {
    setPage((p) => (totalPages ? Math.min(p + 1, totalPages) : p + 1));
  }, [totalPages]);

  const prev = useCallback(() => {
    setPage((p) => Math.max(1, p - 1));
  }, []);

  const setPageSize = useCallback(
    (size: number) => {
      const safe = Math.min(Math.max(minPageSize, size), maxPageSize);
      setPageSizeState(safe);
      setPage(1); // reset to first page when page size changes
    },
    [minPageSize, maxPageSize],
  );

  const reset = useCallback(() => {
    setPage(1);
    setPageSizeState(initialPageSize);
  }, [initialPageSize]);

  return { page, pageIndex, pageSize, total, totalPages, setPage, setPageSize, next, prev, reset };
}

export default usePagination;
