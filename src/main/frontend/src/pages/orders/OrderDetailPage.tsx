import React from 'react';
import { useParams, Link } from 'react-router-dom';

import { ShipmentDialog } from './ShipmentDialog';
import { Badge } from '../../components/ui/badge';
import { Button } from '../../components/ui/button';
import useAsync from '../../hooks/useAsync';
import { emitErrorToast } from '../../lib/errors';
import { getBeerOrder, listBeerOrderShipments } from '../../services/beerOrderService';

export function OrderDetailPage() {
  const { id } = useParams();
  const orderId = Number(id);
  const [shipmentDialogOpen, setShipmentDialogOpen] = React.useState(false);

  const {
    data: order,
    loading: loadingOrder,
    error: orderError,
    run: fetchOrder,
  } = useAsync(() => getBeerOrder(orderId), {
    auto: Boolean(id && !Number.isNaN(orderId)),
    deps: [orderId],
    onError: (err) => emitErrorToast({ source: 'OrderDetailPage', error: err }),
  });

  const {
    data: shipments = [],
    loading: loadingShipments,
    run: fetchShipments,
  } = useAsync(() => listBeerOrderShipments(orderId), {
    auto: Boolean(id && !Number.isNaN(orderId)),
    deps: [orderId],
    onError: (err) => emitErrorToast({ source: 'OrderDetailPage (shipments)', error: err }),
  });

  const totalQty = React.useMemo(
    () => (order?.lines ?? []).reduce((sum, l) => sum + (l.orderQuantity ?? 0), 0),
    [order],
  );

  if (!id || Number.isNaN(orderId)) {
    return <div className="text-sm text-destructive">Invalid order id.</div>;
  }

  if (loadingOrder) {
    return <div className="text-sm text-muted-foreground">Loading order…</div>;
  }

  if (orderError) {
    return (
      <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
        Failed to load order.
        <button
          type="button"
          className="ml-3 inline-flex items-center rounded-md border px-3 py-1.5 text-xs hover:bg-accent"
          onClick={() => fetchOrder()}
        >
          Retry
        </button>
      </div>
    );
  }

  if (!order) {
    return <div className="text-sm text-muted-foreground">Order not found.</div>;
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Order #{order.id}</h1>
          <p className="text-sm text-muted-foreground">Status: {order.status ?? '—'}</p>
        </div>
        <div className="flex gap-2">
          <Link
            to="/orders"
            className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
          >
            Back
          </Link>
          <Link
            to={`/orders/${order.id}/edit`}
            className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
          >
            Edit
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <DetailItem label="Customer Ref" value={order.customerRef} />
        <DetailItem
          label="Payment Amount"
          value={order.paymentAmount != null ? `$${order.paymentAmount.toFixed(2)}` : undefined}
        />
        <DetailItem label="Total Quantity" value={totalQty.toString()} />
        <DetailItem label="Created" value={formatDateTime(order.createdDate)} />
        <DetailItem label="Updated" value={formatDateTime(order.updatedDate)} />
      </div>

      <div>
        <div className="text-sm font-medium mb-2">Items</div>
        <div className="overflow-x-auto rounded-md border">
          <table className="w-full text-sm">
            <thead className="bg-muted/50">
              <tr className="text-left">
                <th className="px-3 py-2">Line #</th>
                <th className="px-3 py-2">Beer</th>
                <th className="px-3 py-2">Quantity</th>
                <th className="px-3 py-2">Allocated</th>
                <th className="px-3 py-2">Status</th>
              </tr>
            </thead>
            <tbody>
              {(order.lines ?? []).length === 0 ? (
                <tr>
                  <td className="px-3 py-3 text-muted-foreground" colSpan={5}>
                    No items.
                  </td>
                </tr>
              ) : (
                (order.lines ?? []).map((line, idx) => (
                  <tr key={line.id ?? idx} className="border-t">
                    <td className="px-3 py-2">{line.id ?? idx + 1}</td>
                    <td className="px-3 py-2">{line.beerName ?? `Beer #${line.beerId}`}</td>
                    <td className="px-3 py-2">{line.orderQuantity ?? 0}</td>
                    <td className="px-3 py-2">{line.quantityAllocated ?? 0}</td>
                    <td className="px-3 py-2">{line.status ?? '—'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <div>
        <div className="text-sm font-medium mb-1">Status history</div>
        <div className="rounded-md border p-3 text-sm text-muted-foreground">
          {/* Backend doesn't expose full history; show created/updated as a minimal timeline */}
          <ul className="list-disc pl-5 space-y-1">
            <li>Created: {formatDateTime(order.createdDate) ?? '—'}</li>
            <li>Last Updated: {formatDateTime(order.updatedDate) ?? '—'}</li>
          </ul>
        </div>
      </div>

      <div>
        <div className="flex items-center justify-between mb-2">
          <div className="text-sm font-medium">Shipments</div>
          <Button variant="outline" size="sm" onClick={() => setShipmentDialogOpen(true)}>
            Add Shipment
          </Button>
        </div>
        <div className="rounded-md border overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-muted/50">
              <tr className="text-left">
                <th className="px-3 py-2">ID</th>
                <th className="px-3 py-2">Tracking Number</th>
                <th className="px-3 py-2">Status</th>
                <th className="px-3 py-2">Created</th>
              </tr>
            </thead>
            <tbody>
              {loadingShipments && (
                <tr>
                  <td className="px-3 py-3 text-muted-foreground" colSpan={4}>
                    Loading shipments…
                  </td>
                </tr>
              )}
              {!loadingShipments && shipments.length === 0 ? (
                <tr>
                  <td className="px-3 py-3 text-muted-foreground" colSpan={4}>
                    No shipments found.
                  </td>
                </tr>
              ) : (
                shipments.map((s) => (
                  <tr key={s.id} className="border-t">
                    <td className="px-3 py-2">{s.id}</td>
                    <td className="px-3 py-2 font-mono text-xs">{s.trackingNumber}</td>
                    <td className="px-3 py-2">
                      <Badge variant="outline">{s.shipmentStatus ?? 'SHIPPED'}</Badge>
                    </td>
                    <td className="px-3 py-2">{formatDateTime(s.createdDate)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <ShipmentDialog
        orderId={orderId}
        open={shipmentDialogOpen}
        onOpenChange={setShipmentDialogOpen}
        onSuccess={() => {
          fetchOrder();
          fetchShipments();
        }}
      />
    </div>
  );
}

function DetailItem({ label, value }: { label: string; value?: string | number | null }) {
  return (
    <div className="space-y-1">
      <div className="text-xs text-muted-foreground">{label}</div>
      <div className="rounded-md border p-3 text-sm min-h-9">{value ?? '-'}</div>
    </div>
  );
}

function formatDateTime(iso?: string) {
  if (!iso) return undefined;
  try {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return iso;
    return d.toLocaleString();
  } catch {
    return iso;
  }
}
