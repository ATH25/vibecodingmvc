import React from 'react';
import { NavLink } from 'react-router-dom';

export function Sidebar() {
  const links = [
    { to: '/', label: 'Home' },
    { to: '/beers', label: 'Beers' },
    { to: '/customers', label: 'Customers' },
    { to: '/orders', label: 'Orders' },
  ];

  return (
    <nav className="p-4 text-sm">
      <ul className="space-y-1">
        {links.map((l) => (
          <li key={l.to}>
            <NavLink
              to={l.to}
              className={({ isActive }) =>
                `block rounded px-3 py-2 hover:bg-accent ${isActive ? 'bg-accent font-medium' : ''}`
              }
            >
              {l.label}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  );
}
