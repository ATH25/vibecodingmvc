import * as React from 'react';

import { Button } from './button';
import { cn } from '../../lib/utils';

export interface PaginationProps {
  page: number;
  pageSize: number;
  total: number;
  onPageChange?: (page: number) => void;
  className?: string;
  showInfo?: boolean;
}

export function Pagination({
  page,
  pageSize,
  total,
  onPageChange,
  className,
  showInfo = true,
}: PaginationProps) {
  const totalPages = Math.max(1, Math.ceil(Math.max(0, total) / Math.max(1, pageSize)));
  const canPrev = page > 1;
  const canNext = page < totalPages;

  return (
    <div className={cn('flex w-full items-center justify-between gap-2', className)}>
      {showInfo ? (
        <div className="text-xs text-muted-foreground">
          Page {page} of {totalPages}
        </div>
      ) : (
        <div />
      )}
      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          size="sm"
          disabled={!canPrev}
          onClick={() => canPrev && onPageChange?.(page - 1)}
        >
          Prev
        </Button>
        <Button
          variant="outline"
          size="sm"
          disabled={!canNext}
          onClick={() => canNext && onPageChange?.(page + 1)}
        >
          Next
        </Button>
      </div>
    </div>
  );
}

Pagination.displayName = 'Pagination';

export default Pagination;
