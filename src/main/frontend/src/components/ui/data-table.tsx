import * as React from 'react';

import { Button } from './button';
import { Checkbox } from './checkbox';
import { cn } from '../../lib/utils';

export type SortDirection = 'asc' | 'desc' | null;

export type ColumnDef<T> = {
  key: keyof T | string;
  header: React.ReactNode | string;
  className?: string;
  cell?: (row: T) => React.ReactNode;
  sortable?: boolean;
};

export interface PaginationProps {
  page: number;
  pageSize: number;
  total: number;
  onPageChange?: (page: number) => void;
}

export interface SelectionProps<T> {
  getRowId?: (row: T) => string | number;
  selectedIds?: Array<string | number>;
  onSelectionChange?: (ids: Array<string | number>) => void;
}

export interface SortingProps {
  sortBy?: string;
  sortDir?: SortDirection;
  onSortChange?: (sortBy: string, dir: Exclude<SortDirection, null>) => void;
}

export interface DataTableProps<T> extends SortingProps, SelectionProps<T> {
  data: T[];
  columns: Array<ColumnDef<T>>;
  className?: string;
  loading?: boolean;
  emptyState?: React.ReactNode;
  pagination?: PaginationProps;
}

export function DataTable<T>({
  data,
  columns,
  className,
  loading,
  emptyState = <div className="p-6 text-sm text-muted-foreground">No data</div>,
  pagination,
  sortBy,
  sortDir,
  onSortChange,
  getRowId = (row) => (row as unknown as { id?: string | number }).id ?? JSON.stringify(row),
  selectedIds,
  onSelectionChange,
}: DataTableProps<T>) {
  const allIds = React.useMemo(() => data.map((r) => getRowId(r)), [data, getRowId]);
  const allSelected = !!selectedIds?.length && allIds.every((id) => selectedIds!.includes(id));
  const someSelected = !!selectedIds?.length && !allSelected;

  const toggleAll = () => {
    if (!onSelectionChange) return;
    if (allSelected) onSelectionChange([]);
    else onSelectionChange(allIds);
  };

  const toggleOne = (id: string | number) => {
    if (!onSelectionChange) return;
    const current = new Set(selectedIds ?? []);
    if (current.has(id)) current.delete(id);
    else current.add(id);
    onSelectionChange(Array.from(current));
  };

  const renderHeaderCell = (col: ColumnDef<T>) => {
    const isSorted = sortBy === col.key;
    const dir = isSorted ? sortDir : null;

    if (!col.sortable || !onSortChange) {
      return <span className="whitespace-nowrap">{col.header}</span>;
    }
    const nextDir: Exclude<SortDirection, null> = isSorted
      ? dir === 'asc'
        ? 'desc'
        : 'asc'
      : 'asc';
    return (
      <button
        type="button"
        className={cn('inline-flex items-center gap-1 whitespace-nowrap hover:underline')}
        onClick={() => onSortChange(col.key as string, nextDir)}
      >
        <span>{col.header}</span>
        <span className="text-muted-foreground">
          {isSorted ? (dir === 'asc' ? '▲' : '▼') : '↕'}
        </span>
      </button>
    );
  };

  return (
    <div className={cn('w-full overflow-x-auto rounded-md border', className)}>
      <table className="w-full text-sm">
        <thead className="bg-muted/50">
          <tr className="border-b">
            {onSelectionChange && (
              <th className="w-12 p-3 align-middle">
                <Checkbox
                  aria-label="Select all"
                  checked={allSelected}
                  indeterminate={someSelected}
                  onCheckedChange={toggleAll}
                />
              </th>
            )}
            {columns.map((col) => (
              <th key={String(col.key)} className={cn('p-3 text-left font-medium', col.className)}>
                {renderHeaderCell(col)}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr>
              <td
                className="p-6 text-center text-muted-foreground"
                colSpan={columns.length + (onSelectionChange ? 1 : 0)}
              >
                Loading...
              </td>
            </tr>
          ) : data.length === 0 ? (
            <tr>
              <td colSpan={columns.length + (onSelectionChange ? 1 : 0)}>{emptyState}</td>
            </tr>
          ) : (
            data.map((row, idx) => {
              const id = getRowId(row);
              const selected = selectedIds?.includes(id) ?? false;
              return (
                <tr
                  key={String(id) || idx}
                  className={cn('border-b last:border-0', selected && 'bg-muted/30')}
                >
                  {onSelectionChange && (
                    <td className="p-3 align-middle">
                      <Checkbox
                        aria-label={`Select row ${idx + 1}`}
                        checked={selected}
                        onCheckedChange={() => toggleOne(id)}
                      />
                    </td>
                  )}
                  {columns.map((col) => (
                    <td key={String(col.key)} className={cn('p-3 align-middle', col.className)}>
                      {col.cell ? col.cell(row) : String((row as any)[col.key as keyof T] ?? '')}
                    </td>
                  ))}
                </tr>
              );
            })
          )}
        </tbody>
      </table>

      {pagination && (
        <div className="flex items-center justify-between gap-2 p-3">
          <div className="text-xs text-muted-foreground">
            Page {pagination.page} of{' '}
            {Math.max(1, Math.ceil(pagination.total / Math.max(1, pagination.pageSize)))}
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              disabled={pagination.page <= 1}
              onClick={() => pagination.onPageChange?.(pagination.page - 1)}
            >
              Prev
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={pagination.page >= Math.ceil(pagination.total / pagination.pageSize)}
              onClick={() => pagination.onPageChange?.(pagination.page + 1)}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}

// Minimal Checkbox using shadcn style if not present, fallback implementation
// The project already includes many ui primitives. If a dedicated checkbox exists, this import above will resolve to it.
// If not, provide a light inline version here.
