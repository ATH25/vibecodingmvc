import React from 'react';

type EmptyStateProps = {
  title?: string;
  description?: string;
  action?: React.ReactNode;
};

export function EmptyState({
  title = 'No data',
  description = 'There is nothing to show here yet.',
  action,
}: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 py-16 text-center">
      <div className="size-12 rounded-full border flex items-center justify-center text-muted-foreground">
        Ø
      </div>
      <h2 className="text-lg font-medium">{title}</h2>
      <p className="max-w-md text-sm text-muted-foreground">{description}</p>
      {action && <div className="mt-2">{action}</div>}
    </div>
  );
}

