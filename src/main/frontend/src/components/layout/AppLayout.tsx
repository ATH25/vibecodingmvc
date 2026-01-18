import React from 'react';

import { Footer } from './Footer';
import { Navbar } from './Navbar';
import { Sidebar } from './Sidebar';
import { Breadcrumbs } from '../navigation/Breadcrumbs';
import { Toaster } from '../ui/toaster';

type AppLayoutProps = {
  children: React.ReactNode;
  showSidebar?: boolean;
};

export default function AppLayout({ children, showSidebar = false }: AppLayoutProps) {
  return (
    <div className="min-h-screen bg-background text-foreground flex flex-col">
      <Navbar />
      <div className="flex flex-1">
        {showSidebar && (
          <aside className="hidden md:block w-64 border-r bg-card">
            <Sidebar />
          </aside>
        )}
        <main className="flex-1 container mx-auto px-4 py-6">
          <Breadcrumbs />
          {children}
        </main>
      </div>
      <Footer />
      {/* Global toaster for notifications */}
      <Toaster />
    </div>
  );
}
