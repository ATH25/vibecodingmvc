import { render, screen, act } from '@testing-library/react';
import React from 'react';
import { MemoryRouter } from 'react-router-dom';

import { BeerListPage } from './beers/BeerListPage';
import { CustomerListPage } from './customers/CustomerListPage';
import { BeerOrderListPage } from './orders/BeerOrderListPage';

// Mock the services
jest.mock('../services/beerService', () => ({
  listBeers: jest.fn(() =>
    Promise.resolve({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 }),
  ),
}));
jest.mock('../services/customerService', () => ({
  listCustomers: jest.fn(() => Promise.resolve([])),
}));
jest.mock('../services/beerOrderService', () => ({
  listBeerOrders: jest.fn(() =>
    Promise.resolve({ content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 }),
  ),
}));

// Mock custom hooks that might use context or complex logic
jest.mock('../hooks/useQueryParams', () => {
  const mockParams = { params: { page: '1', size: '10' }, get: jest.fn(), setParams: jest.fn() };
  return {
    __esModule: true,
    default: jest.fn(() => mockParams),
    useQueryParams: jest.fn(() => mockParams),
  };
});

describe('Smoke Tests', () => {
  it('renders BeerListPage without crashing', async () => {
    await act(async () => {
      render(
        <MemoryRouter>
          <BeerListPage />
        </MemoryRouter>,
      );
    });
    expect(screen.getByRole('heading', { name: /Beers/i, level: 1 })).toBeInTheDocument();
  });

  it('renders CustomerListPage without crashing', async () => {
    await act(async () => {
      render(
        <MemoryRouter>
          <CustomerListPage />
        </MemoryRouter>,
      );
    });
    expect(screen.getByRole('heading', { name: /Customers/i, level: 1 })).toBeInTheDocument();
  });

  it('renders BeerOrderListPage without crashing', async () => {
    await act(async () => {
      render(
        <MemoryRouter>
          <BeerOrderListPage />
        </MemoryRouter>,
      );
    });
    expect(screen.getByRole('heading', { name: /Orders/i, level: 1 })).toBeInTheDocument();
  });
});
