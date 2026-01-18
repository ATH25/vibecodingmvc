import React from 'react';

type State = { hasError: boolean; message?: string };

export class AppErrorBoundary extends React.Component<React.PropsWithChildren, State> {
  constructor(props: React.PropsWithChildren) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: unknown): State {
    return { hasError: true, message: (error as any)?.message || 'Unexpected error' };
  }

  override componentDidCatch(error: unknown, errorInfo: unknown) {
    // eslint-disable-next-line no-console
    console.error('UI ErrorBoundary caught error', error, errorInfo);
  }

  override render() {
    if (this.state.hasError) {
      return (
        <div className="container mx-auto px-4 py-10">
          <div className="rounded-md border p-6">
            <h2 className="text-lg font-semibold mb-1">Something went wrong</h2>
            <p className="text-sm text-muted-foreground">{this.state.message}</p>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}
