import React from 'react';

import { useTheme } from '../theme/ThemeProvider';

export function Navbar() {
  const { resolvedTheme, setTheme, theme } = useTheme();
  const nextTheme = resolvedTheme === 'dark' ? 'light' : 'dark';

  return (
    <header className="w-full border-b bg-background/80 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container mx-auto h-14 px-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="size-6 rounded bg-primary" />
          <span className="font-semibold">VibeCoding</span>
        </div>
        <div className="flex items-center gap-2">
          <button
            type="button"
            aria-label="Toggle Theme"
            className="inline-flex items-center rounded-md border px-3 py-1.5 text-sm hover:bg-accent"
            onClick={() => setTheme(theme === 'system' ? nextTheme : nextTheme)}
          >
            {resolvedTheme === 'dark' ? 'Light' : 'Dark'}
          </button>
        </div>
      </div>
    </header>
  );
}
