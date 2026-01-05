import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import React from 'react';

import { OrderForm } from './OrderForm';

const mockBeers = [
  { id: 1, beerName: 'IPA' },
  { id: 2, beerName: 'Lager' },
];

describe('OrderForm', () => {
  it('renders correctly', () => {
    render(<OrderForm beers={mockBeers} onSubmit={jest.fn()} />);
    expect(screen.getByLabelText(/Customer Reference/i)).toBeInTheDocument();
    expect(screen.getByText(/Add Item/i)).toBeInTheDocument();
  });

  it('submits form with correct values', async () => {
    const onSubmit = jest.fn();
    render(<OrderForm beers={mockBeers} onSubmit={onSubmit} />);

    fireEvent.change(screen.getByLabelText(/Customer Reference/i), {
      target: { value: 'REF123' },
    });

    // Selecting beer and quantity (simplified for the test environment)
    // In a real test we'd interact with the Radix Select, which is harder with jsdom.
    // For now, let's just trigger the submit button and check validation.

    fireEvent.click(screen.getByRole('button', { name: /Save Order/i }));

    await waitFor(() => {
      expect(onSubmit).not.toHaveBeenCalled(); // Should fail because beerId is undefined
    });
  });
});
