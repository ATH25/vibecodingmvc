import * as React from 'react';
import { Link } from 'react-router-dom';

import { EmptyState } from '../../components/common/EmptyState';
import { Button } from '../../components/ui/button';
import { ConfirmDialog } from '../../components/ui/confirm-dialog';
import { DataTable, type ColumnDef } from '../../components/ui/data-table';
import { Input } from '../../components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui/select';
import useBeers from '../../hooks/useBeers';
import useToast from '../../hooks/useToast';
import { fromAxiosError } from '../../lib/errors';
import { deleteBeer } from '../../services/beerService';

type BeerRow = {
  id: number;
  beerName: string;
  beerStyle?: string;
  upc?: string;
  price?: number;
  quantityOnHand?: number;
};

const BEER_STYLES = [
  'ALE',
  'PALE_ALE',
  'IPA',
  'LAGER',
  'PILSNER',
  'STOUT',
  'PORTER',
  'WHEAT',
  'SOUR',
];

export function BeerListPage() {
  const {
    beers,
    pagination,
    loading,
    error,
    filters,
    sort,
    fetchBeers,
    setData,
    handlePageChange,
    handleFilterChange,
    handleSortChange,
  } = useBeers();
  const { success, error: toastError } = useToast();

  const beerName = filters.beerName;
  const beerStyle = filters.beerStyle;

  // Local state for delete confirmation
  const [deletingId, setDeletingId] = React.useState<number | null>(null);

  const handleDelete = React.useCallback(
    async (row: BeerRow) => {
      // optimistic update
      setDeletingId(row.id);
      try {
        setData((curr) =>
          curr
            ? {
                ...curr,
                content: curr.content.filter((b) => b.id !== row.id),
                totalElements: Math.max(0, (curr.totalElements ?? 0) - 1),
              }
            : curr,
        );
        await deleteBeer(row.id);
        success({ title: 'Deleted', description: `Beer "${row.beerName}" was deleted.` });
        // refetch to ensure pagination counts are accurate
        await fetchBeers();
      } catch (err) {
        // rollback by refetching
        await fetchBeers();
        const appErr = fromAxiosError(err);
        if (appErr.status === 409) {
          toastError({
            title: 'Delete conflict',
            description: `Beer "${row.beerName}" cannot be deleted due to a conflict.`,
          });
        } else if (appErr.status === 400) {
          toastError({
            title: 'Bad request',
            description: `Delete request for beer "${row.beerName}" is invalid.`,
          });
        } else {
          toastError({ title: 'Failed to delete', description: appErr.message });
        }
      } finally {
        setDeletingId(null);
      }
    },
    [fetchBeers, setData, success, toastError],
  );

  // Columns definition for the table
  const columns = React.useMemo<ColumnDef<BeerRow>[]>(
    () => [
      { key: 'beerName', header: 'Name', sortable: true },
      { key: 'beerStyle', header: 'Style', sortable: true },
      { key: 'upc', header: 'UPC', sortable: true },
      {
        key: 'price',
        header: 'Price',
        cell: (r) => (r.price != null ? `$${r.price.toFixed(2)}` : '-'),
        sortable: true,
      },
      { key: 'quantityOnHand', header: 'Qty', sortable: true },
      {
        key: 'actions',
        header: 'Actions',
        cell: (r) => (
          <div className="flex gap-2">
            <Link to={`/beers/${r.id}`} className="text-primary hover:underline text-sm">
              View
            </Link>
            <Link
              to={`/beers/${r.id}/edit`}
              className="text-muted-foreground hover:underline text-sm"
            >
              Edit
            </Link>
            <ConfirmDialog
              title="Delete beer?"
              description={`This will permanently delete "${r.beerName}".`}
              confirmText="Delete"
              confirmVariant="destructive"
              onConfirm={() => handleDelete(r)}
              disabled={deletingId === r.id}
            >
              <button
                type="button"
                className="text-destructive hover:underline text-sm disabled:opacity-60"
                disabled={deletingId === r.id}
              >
                {deletingId === r.id ? 'Deleting…' : 'Delete'}
              </button>
            </ConfirmDialog>
          </div>
        ),
      },
    ],
    [deletingId, handleDelete],
  );

  const rows: BeerRow[] = beers.map((b) => ({
    id: b.id!,
    beerName: b.beerName ?? '',
    beerStyle: b.beerStyle,
    upc: b.upc,
    price: b.price,
    quantityOnHand: b.quantityOnHand,
  }));

  const onSubmitFilters = (e: React.FormEvent) => {
    e.preventDefault();
    // handleFilterChange already resets page to 1
  };

  const onClear = () => {
    handleFilterChange({ beerName: '', beerStyle: '' });
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">Beers</h1>
        <Link
          to="/beers/create"
          className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
        >
          Create Beer
        </Link>
      </div>

      {error ? (
        <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          Failed to load beers.
          <Button variant="outline" size="sm" className="ml-3" onClick={() => fetchBeers()}>
            Retry
          </Button>
        </div>
      ) : null}

      {/* Filters */}
      <form onSubmit={onSubmitFilters} className="grid grid-cols-1 gap-3 sm:grid-cols-3">
        <div className="space-y-1">
          <label className="text-sm text-muted-foreground" htmlFor="beerName">
            Name
          </label>
          <Input
            id="beerName"
            placeholder="Search by name"
            value={beerName}
            onChange={(e) => handleFilterChange({ beerName: e.target.value })}
          />
        </div>

        <div className="space-y-1">
          <label className="text-sm text-muted-foreground">Style</label>
          <Select value={beerStyle} onValueChange={(val) => handleFilterChange({ beerStyle: val })}>
            <SelectTrigger>
              <SelectValue placeholder="All styles" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value=" ">All</SelectItem>
              {BEER_STYLES.map((s) => (
                <SelectItem key={s} value={s}>
                  {s.replace('_', ' ')}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="flex items-end gap-2">
          <Button type="button" variant="outline" onClick={onClear}>
            Clear
          </Button>
        </div>
      </form>

      {/* Table */}
      <DataTable<BeerRow>
        data={rows}
        columns={columns}
        loading={loading}
        emptyState={<EmptyState title="No beers" description="No matching beers found." />}
        pagination={{
          page: pagination.page,
          pageSize: pagination.size,
          total: pagination.totalElements,
          onPageChange: handlePageChange,
        }}
        sortBy={sort?.split(',')[0]}
        sortDir={(sort?.split(',')[1] as any) || null}
        onSortChange={handleSortChange}
      />
    </div>
  );
}
