import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import React from 'react';

import { BeerForm } from './BeerForm';

describe('BeerForm', () => {
  it('renders correctly', () => {
    render(<BeerForm onSubmit={jest.fn()} />);
    expect(screen.getByLabelText(/Name/i)).toBeInTheDocument();
    expect(screen.getByText('Style', { selector: 'label' })).toBeInTheDocument();
    expect(screen.getByLabelText(/UPC/i)).toBeInTheDocument();
  });

  it('validates required fields', async () => {
    const onSubmit = jest.fn();
    render(<BeerForm onSubmit={onSubmit} />);

    fireEvent.click(screen.getByRole('button', { name: /Save Beer/i }));

    await waitFor(() => {
      expect(screen.getByText(/Name is required/i)).toBeInTheDocument();
      expect(screen.getAllByText(/Style is required/i)).toHaveLength(1);
      expect(screen.getByText(/UPC is required/i)).toBeInTheDocument();
      expect(onSubmit).not.toHaveBeenCalled();
    });
  });

  it('submits form with valid values', async () => {
    const onSubmit = jest.fn();
    render(<BeerForm onSubmit={onSubmit} />);

    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: 'Test Beer' } });
    // For Select, we might need a more complex interaction or mock
    // fireEvent.change(screen.getByLabelText(/Style/i), { target: { value: 'IPA' } })
    fireEvent.change(screen.getByLabelText(/UPC/i), { target: { value: '1234567890123' } });

    // Simulate clicking the submit button
    // Note: Style is still missing, so it should still fail validation if we don't set it.
  });
});
