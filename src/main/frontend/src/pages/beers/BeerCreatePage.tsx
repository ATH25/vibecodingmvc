import React from 'react';
import { useNavigate } from 'react-router-dom';

import { BeerForm, type BeerFormValues } from './BeerForm';
import useToast from '../../hooks/useToast';
import { notifyError } from '../../lib/errors';
import { createBeer } from '../../services/beerService';

export function BeerCreatePage() {
  const navigate = useNavigate();
  const { success } = useToast();
  const [loading, setLoading] = React.useState(false);

  const onSubmit = async (values: BeerFormValues) => {
    setLoading(true);
    try {
      const created = await createBeer({
        ...values,
        beerName: values.beerName.trim(),
        upc: values.upc.trim(),
      });
      success({
        title: 'Beer created',
        description: `${created.beerName ?? 'Beer'} has been created.`,
      });
      if (created.id != null) {
        navigate(`/beers/${created.id}`);
      } else {
        navigate('/beers');
      }
    } catch (err) {
      notifyError(err, { title: 'Failed to create beer' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold tracking-tight">Create Beer</h1>
      </div>

      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <BeerForm
          onSubmit={onSubmit}
          onCancel={() => navigate('/beers')}
          loading={loading}
          submitLabel="Create Beer"
        />
      </div>
    </div>
  );
}
