import { type LucideIcon, PackageSearch } from 'lucide-react';
import type { ReactNode } from 'react';

type EmptyStateProps = {
  title?: string;
  description?: string;
  action?: ReactNode;
  icon?: LucideIcon;
};

export function EmptyState({
  title = 'No data',
  description = 'There is nothing to show here yet.',
  action,
  icon: Icon = PackageSearch,
}: EmptyStateProps) {
  return (
    <div
      className="flex flex-col items-center justify-center gap-2 py-16 text-center"
      role="region"
      aria-label={title}
    >
      <div
        className="flex size-12 items-center justify-center rounded-full border text-muted-foreground"
        aria-hidden="true"
      >
        <Icon className="size-6" />
      </div>
      <h2 className="text-lg font-medium">{title}</h2>
      <p className="max-w-md text-sm text-muted-foreground">{description}</p>
      {action && <div className="mt-2">{action}</div>}
    </div>
  );
}
