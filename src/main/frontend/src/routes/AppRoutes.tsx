import React from 'react';
import { Route, Routes, Outlet, Navigate } from 'react-router-dom';

import AppLayout from '../components/layout/AppLayout';
import { BeerCreatePage } from '../pages/beers/BeerCreatePage';
import { BeerDetailPage } from '../pages/beers/BeerDetailPage';
import { BeerEditPage } from '../pages/beers/BeerEditPage';
import { BeerListPage } from '../pages/beers/BeerListPage';
import { CustomerCreatePage } from '../pages/customers/CustomerCreatePage';
import { CustomerDetailPage } from '../pages/customers/CustomerDetailPage';
import { CustomerEditPage } from '../pages/customers/CustomerEditPage';
import { CustomerListPage } from '../pages/customers/CustomerListPage';
import { BeerOrderEditPage } from '../pages/orders/BeerOrderEditPage';
import { BeerOrderListPage } from '../pages/orders/BeerOrderListPage';
import { OrderCreatePage } from '../pages/orders/OrderCreatePage';
import { OrderDetailPage } from '../pages/orders/OrderDetailPage';
import { AppErrorBoundary } from '../routes/AppErrorBoundary';
import { HomePage } from '../routes/HomePage';
import { NotFoundPage } from '../routes/NotFoundPage';

function LayoutOutlet() {
  return (
    <AppLayout showSidebar>
      {/* Error boundary per layout scope */}
      <AppErrorBoundary>
        <Outlet />
      </AppErrorBoundary>
    </AppLayout>
  );
}

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<LayoutOutlet />}>
        <Route index element={<HomePage />} />

        <Route path="beers">
          <Route index element={<BeerListPage />} />
          <Route path="create" element={<BeerCreatePage />} />
          <Route path=":id" element={<BeerDetailPage />} />
          <Route path=":id/edit" element={<BeerEditPage />} />
        </Route>

        <Route path="customers">
          <Route index element={<CustomerListPage />} />
          <Route path="create" element={<CustomerCreatePage />} />
          <Route path=":id" element={<CustomerDetailPage />} />
          <Route path=":id/edit" element={<CustomerEditPage />} />
        </Route>

        <Route path="orders">
          <Route index element={<BeerOrderListPage />} />
          <Route path="create" element={<OrderCreatePage />} />
          <Route path=":id" element={<OrderDetailPage />} />
          <Route path=":id/edit" element={<BeerOrderEditPage />} />
        </Route>

        <Route path="404" element={<NotFoundPage />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Route>
    </Routes>
  );
}
