import { ShoppingBag } from 'lucide-react';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { ShipmentDialog } from './ShipmentDialog';
import { EmptyState } from '../../components/common/EmptyState';
import { Button } from '../../components/ui/button';
import { DataTable, type ColumnDef } from '../../components/ui/data-table';
import { Input } from '../../components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui/select';
import useAsync from '../../hooks/useAsync';
import useQueryParams from '../../hooks/useQueryParams';
import { emitErrorToast, fromAxiosError } from '../../lib/errors';
import { listBeerOrders } from '../../services/beerOrderService';

type OrderRow = {
  id: number;
  customerName: string;
  status: 'PENDING' | 'PAID' | 'CANCELLED';
  createdDate?: string;
  total?: number;
};

const STATUS = ['PENDING', 'PAID', 'CANCELLED'] as const;

export function BeerOrderListPage() {
  const [selectedOrderId, setSelectedOrderId] = React.useState<number | null>(null);

  // Persist filters and table state in the URL for shareable links
  const { params, setParams } = useQueryParams<{
    page?: string;
    size?: string;
    status?: (typeof STATUS)[number] | '';
    customer?: string;
    from?: string;
    to?: string;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
  }>();

  const page = Number(params.page ?? 1);
  const size = Number(params.size ?? 10);
  const status = (params.status as OrderRow['status'] | 'ALL' | '') ?? 'ALL';
  const customer = params.customer ?? '';
  const from = params.from ?? '';
  const to = params.to ?? '';
  const sortBy = params.sortBy ?? 'createdDate';
  const sortDir = (params.sortDir as 'asc' | 'desc' | undefined) ?? 'desc';

  // Load orders; backend returns a paged object, so we'll pass pagination and filters to the server
  const { data, loading, error, run } = useAsync(
    async () =>
      listBeerOrders({
        page,
        size,
        status: (status === 'ALL' || status === '' ? undefined : status) as any,
        sort: sortBy ? `${sortBy},${sortDir}` : undefined,
      }),
    {
      auto: true,
      deps: [page, size, status, sortBy, sortDir],
      onError: (err) => emitErrorToast({ error: fromAxiosError(err) }),
    },
  );

  const columns = React.useMemo<ColumnDef<OrderRow>[]>(
    () => [
      { key: 'id', header: 'Order #', sortable: true },
      { key: 'customerName', header: 'Customer', sortable: true },
      { key: 'status', header: 'Status', sortable: true },
      { key: 'createdDate', header: 'Created', sortable: true },
      {
        key: 'total',
        header: 'Total',
        sortable: true,
        cell: (r) => (r.total != null ? `$${r.total.toFixed(2)}` : ''),
      },
      {
        key: 'actions',
        header: 'Actions',
        cell: (r) => (
          <div className="flex gap-2">
            <Link to={`/orders/${r.id}`} className="text-primary hover:underline text-sm">
              View
            </Link>
            <Link
              to={`/orders/${r.id}/edit`}
              className="text-muted-foreground hover:underline text-sm"
            >
              Edit
            </Link>
            <button
              type="button"
              className="text-muted-foreground hover:underline text-sm"
              onClick={() => setSelectedOrderId(r.id)}
            >
              Shipment
            </button>
          </div>
        ),
      },
    ],
    [],
  );

  const onClear = () =>
    setParams(
      {
        page: 1,
        size: 10,
        status: '',
        customer: '',
        from: '',
        to: '',
        sortBy: 'createdDate',
        sortDir: 'desc',
      },
      'replace',
    );

  const onSortChange = (key: string, dir: 'asc' | 'desc') =>
    setParams({ sortBy: key, sortDir: dir, page: 1 });
  const onPageChange = (next: number) => setParams({ page: Math.max(1, next) });

  // Map API data to table rows and apply client-side filters
  const allRows = React.useMemo<OrderRow[]>(() => {
    const rows = (data?.content ?? []).map((o: any) => ({
      id: o.id as number,
      customerName: (o.customerRef as string) ?? '—',
      status: (o.status as any) ?? 'PENDING',
      createdDate: o.createdDate as string | undefined,
      total: (o.paymentAmount as number | undefined) ?? undefined,
    }));
    // client-side filters for customer name and date range (if not supported by backend)
    const filteredByCustomer = customer
      ? rows.filter((r) => r.customerName.toLowerCase().includes(customer.toLowerCase()))
      : rows;
    const filteredByFrom = from
      ? filteredByCustomer.filter((r) => (r.createdDate ?? '') >= from)
      : filteredByCustomer;
    const filteredByTo = to
      ? filteredByFrom.filter((r) => (r.createdDate ?? '') <= to)
      : filteredByFrom;
    return filteredByTo;
  }, [data, customer, from, to]);

  const total = data?.totalElements ?? allRows.length;
  const pageRows = allRows;

  return (
    <div>
      <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <h1 className="text-xl font-semibold">Orders</h1>
        <div className="flex flex-1 flex-wrap items-end gap-2 md:justify-end">
          <div className="flex items-center gap-2">
            <div className="min-w-[180px]">
              <Select
                value={status}
                onValueChange={(v) => setParams({ status: (v as any) ?? '', page: 1 }, 'replace')}
              >
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">All Status</SelectItem>
                  {STATUS.map((s) => (
                    <SelectItem key={s} value={s}>
                      {s}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <Input
              placeholder="Customer name"
              className="w-[220px]"
              value={customer}
              onChange={(e) => setParams({ customer: e.target.value, page: 1 }, 'replace')}
            />
            <Input
              type="date"
              className="w-[170px]"
              value={from}
              onChange={(e) => setParams({ from: e.target.value, page: 1 }, 'replace')}
            />
            <Input
              type="date"
              className="w-[170px]"
              value={to}
              onChange={(e) => setParams({ to: e.target.value, page: 1 }, 'replace')}
            />
          </div>

          <Button variant="outline" size="sm" onClick={onClear}>
            Clear
          </Button>
          <Link
            to="/orders/create"
            className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
          >
            Create Order
          </Link>
        </div>
      </div>

      {error ? (
        <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          Failed to load orders.
          <Button variant="outline" size="sm" className="ml-3" onClick={() => run()}>
            Retry
          </Button>
        </div>
      ) : null}

      <DataTable<OrderRow>
        data={pageRows}
        columns={columns}
        loading={loading}
        emptyState={
          <EmptyState
            title="No orders yet"
            description="Use the Create button to add your first order."
            icon={ShoppingBag}
          />
        }
        pagination={{ page, pageSize: size, total, onPageChange }}
        sortBy={sortBy}
        sortDir={sortDir}
        onSortChange={onSortChange}
      />

      {selectedOrderId && (
        <ShipmentDialog
          orderId={selectedOrderId}
          open={!!selectedOrderId}
          onOpenChange={(open) => !open && setSelectedOrderId(null)}
          onSuccess={() => run()}
        />
      )}
    </div>
  );
}

export default BeerOrderListPage;
