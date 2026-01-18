import React from 'react';
import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <div className="container mx-auto px-4 py-16 text-center">
      <h1 className="text-2xl font-semibold mb-2">404 - Page Not Found</h1>
      <p className="text-muted-foreground mb-6">The page you are looking for does not exist.</p>
      <Link
        to="/"
        className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
      >
        Go Home
      </Link>
    </div>
  );
}
