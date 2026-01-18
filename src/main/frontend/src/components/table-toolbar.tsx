import * as React from 'react';

import { cn } from '../lib/utils';
import { Button } from './ui/button';
import { Input } from './ui/input';

export interface TableToolbarProps {
  className?: string;
  placeholder?: string;
  search?: string;
  onSearchChange?: (value: string) => void;
  leftActions?: React.ReactNode;
  rightActions?: React.ReactNode;
  selectedCount?: number;
  onClearSelection?: () => void;
}

export function TableToolbar({
  className,
  placeholder = 'Search…',
  search,
  onSearchChange,
  leftActions,
  rightActions,
  selectedCount = 0,
  onClearSelection,
}: TableToolbarProps) {
  const onChange = (e: React.ChangeEvent<HTMLInputElement>) => onSearchChange?.(e.target.value);

  return (
    <div
      className={cn(
        'flex w-full flex-col gap-2 p-3 md:flex-row md:items-center md:justify-between',
        className,
      )}
    >
      <div className="flex flex-1 items-center gap-2">
        <div className="relative w-full max-w-xs">
          <Input
            value={search ?? ''}
            onChange={onChange}
            placeholder={placeholder}
            aria-label="Search table"
          />
        </div>
        {leftActions}
      </div>
      <div className="flex items-center gap-2 md:justify-end">
        {selectedCount > 0 && (
          <div className="flex items-center gap-2 rounded-md border bg-muted/50 px-2 py-1 text-xs text-muted-foreground">
            <span>{selectedCount} selected</span>
            {onClearSelection && (
              <Button variant="ghost" size="sm" onClick={onClearSelection} className="h-7 px-2">
                Clear
              </Button>
            )}
          </div>
        )}
        {rightActions}
      </div>
    </div>
  );
}

TableToolbar.displayName = 'TableToolbar';

export default TableToolbar;
