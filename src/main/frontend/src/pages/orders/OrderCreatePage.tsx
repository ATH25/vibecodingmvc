import React from 'react';
import { useNavigate } from 'react-router-dom';

import { OrderForm, type OrderFormValues } from './OrderForm';
import useAsync from '../../hooks/useAsync';
import useToast from '../../hooks/useToast';
import { notifyError, emitErrorToast } from '../../lib/errors';
import { createBeerOrder } from '../../services/beerOrderService';
import { listBeers } from '../../services/beerService';

export function OrderCreatePage() {
  const navigate = useNavigate();
  const { success } = useToast();
  const [loading, setLoading] = React.useState(false);

  const { data: beers = [] } = useAsync(() => listBeers({ size: 1000, sort: 'beerName,asc' }), {
    auto: true,
    onError: (err) => emitErrorToast({ source: 'OrderCreatePage', error: err }),
  });

  const beerList = (beers as any)?.content ?? (Array.isArray(beers) ? beers : []);

  const onSubmit = async (values: OrderFormValues) => {
    setLoading(true);
    try {
      const payload = {
        customerRef: values.customerRef?.trim() || undefined,
        paymentAmount: values.paymentAmount || 0,
        items: values.items.map((it) => ({
          beerId: it.beerId,
          quantity: it.quantity,
        })),
      };

      const created = await createBeerOrder(payload);
      success({
        title: 'Order created',
        description: `Order #${created.id} has been created.`,
      });
      navigate(`/orders/${created.id}`);
    } catch (err) {
      notifyError(err, { title: 'Failed to create order' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold tracking-tight">Create Order</h1>
      </div>

      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <OrderForm
          beers={beerList}
          onSubmit={onSubmit}
          onCancel={() => navigate('/orders')}
          loading={loading}
          submitLabel="Create Order"
        />
      </div>
    </div>
  );
}
