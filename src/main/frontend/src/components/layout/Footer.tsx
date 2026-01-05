import React from 'react';

export function Footer() {
  return (
    <footer className="w-full border-t py-4 text-sm text-muted-foreground">
      <div className="container mx-auto px-4 flex items-center justify-between">
        <span>© {new Date().getFullYear()} VibeCoding</span>
        <span className="hidden sm:inline">Built with React, Vite, and shadcn/ui</span>
      </div>
    </footer>
  );
}
