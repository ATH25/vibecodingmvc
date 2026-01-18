import React from 'react';
import { Link, useLocation } from 'react-router-dom';

function toTitle(segment: string) {
  if (!segment) return '';
  if (/^\d+$/.test(segment)) return segment;
  return segment.replace(/[-_]/g, ' ').replace(/\b\w/g, (m) => m.toUpperCase());
}

export function Breadcrumbs() {
  const location = useLocation();
  const parts = location.pathname.split('/').filter(Boolean);

  const items = [
    { label: 'Home', to: '/' },
    ...parts.map((part, idx) => {
      const to = '/' + parts.slice(0, idx + 1).join('/');
      return { label: toTitle(part), to };
    }),
  ];

  return (
    <nav className="mb-4 text-sm text-muted-foreground" aria-label="Breadcrumb">
      <ol className="flex flex-wrap items-center gap-1">
        {items.map((item, i) => {
          const isLast = i === items.length - 1;
          return (
            <li key={i} className="flex items-center">
              {i > 0 && <span className="px-1">/</span>}
              {isLast ? (
                <span className="text-foreground">{item.label}</span>
              ) : (
                <Link to={item.to} className="hover:underline">
                  {item.label}
                </Link>
              )}
            </li>
          );
        })}
      </ol>
    </nav>
  );
}
