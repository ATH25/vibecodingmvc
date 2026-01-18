import React, { useMemo } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { OrderForm, type OrderFormValues } from './OrderForm';
import { Button } from '../../components/ui/button';
import useAsync from '../../hooks/useAsync';
import useToast from '../../hooks/useToast';
import { notifyError, emitErrorToast } from '../../lib/errors';
import { getBeerOrder, updateBeerOrder } from '../../services/beerOrderService';
import { listBeers } from '../../services/beerService';

export function BeerOrderEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { success } = useToast();

  const orderId = useMemo(() => Number(id), [id]);

  const {
    data: order,
    loading: loadingOrder,
    error: orderError,
    run: fetchOrder,
  } = useAsync(() => getBeerOrder(orderId), {
    auto: !!id && !Number.isNaN(orderId),
    deps: [orderId],
    onError: (err) => emitErrorToast({ source: 'BeerOrderEditPage', error: err }),
  });

  const { data: beers = [] } = useAsync(() => listBeers({ size: 1000, sort: 'beerName,asc' }), {
    auto: true,
    onError: (err) => emitErrorToast({ source: 'BeerOrderEditPage (beers)', error: err }),
  });

  const beerList = (beers as any)?.content ?? (Array.isArray(beers) ? beers : []);

  const [saving, setSaving] = React.useState(false);

  const onSubmit = async (values: OrderFormValues) => {
    if (!id || Number.isNaN(orderId)) return;
    setSaving(true);
    try {
      const payload = {
        customerRef: values.customerRef?.trim() || undefined,
        paymentAmount: values.paymentAmount || 0,
        items: values.items.map((it) => ({
          beerId: it.beerId,
          quantity: it.quantity,
        })),
      };

      await updateBeerOrder(orderId, payload);
      success({ title: 'Order updated', description: 'Changes saved successfully.' });
      navigate(`/orders/${orderId}`);
    } catch (err) {
      notifyError(err, { title: 'Failed to update order' });
    } finally {
      setSaving(false);
    }
  };

  if (!id || Number.isNaN(orderId)) {
    return (
      <div className="rounded-lg border border-destructive/20 bg-destructive/10 p-4 text-destructive">
        Invalid order id.
      </div>
    );
  }

  if (loadingOrder && !order) {
    return (
      <div className="flex h-32 items-center justify-center">
        <span className="text-muted-foreground">Loading order...</span>
      </div>
    );
  }

  if (orderError && !order) {
    return (
      <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
        Failed to load order details.
        <Button variant="outline" size="sm" className="ml-3" onClick={() => fetchOrder()}>
          Retry
        </Button>
      </div>
    );
  }

  if (!order && !loadingOrder) {
    return (
      <div className="rounded-lg border border-destructive/20 bg-destructive/10 p-4 text-destructive">
        Order not found.
      </div>
    );
  }

  const initialValues: Partial<OrderFormValues> = {
    customerRef: order?.customerRef ?? '',
    paymentAmount: order?.paymentAmount ?? 0,
    items:
      order?.lines?.map((line) => ({
        beerId: line.beerId!,
        quantity: line.orderQuantity ?? 1,
      })) || [],
  };

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold tracking-tight">Edit Order #{orderId}</h1>
      </div>

      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <OrderForm
          beers={beerList}
          initialValues={initialValues}
          onSubmit={onSubmit}
          onCancel={() => navigate(`/orders/${orderId}`)}
          loading={saving}
          submitLabel="Save Changes"
        />
      </div>
    </div>
  );
}
