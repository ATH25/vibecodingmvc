import React from 'react';
import { Link } from 'react-router-dom';

export function HomePage() {
  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-semibold">Welcome to VibeCoding</h1>
      <p className="text-muted-foreground">Choose a module to get started.</p>
      <div className="flex gap-3">
        <Link
          to="/beers"
          className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
        >
          Beers
        </Link>
        <Link
          to="/customers"
          className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
        >
          Customers
        </Link>
        <Link
          to="/orders"
          className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
        >
          Orders
        </Link>
      </div>
    </div>
  );
}
