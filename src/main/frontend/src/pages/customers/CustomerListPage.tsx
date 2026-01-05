import React from 'react';
import { Link } from 'react-router-dom';

import { EmptyState } from '../../components/common/EmptyState';
import { Button } from '../../components/ui/button';
import { ConfirmDialog } from '../../components/ui/confirm-dialog';
import { DataTable, type ColumnDef } from '../../components/ui/data-table';
import { Input } from '../../components/ui/input';
import useAsync from '../../hooks/useAsync';
import useQueryParams from '../../hooks/useQueryParams';
import useToast from '../../hooks/useToast';
import { emitErrorToast, fromAxiosError } from '../../lib/errors';
import { listCustomers, deleteCustomer } from '../../services/customerService';

type CustomerRow = {
  id: number;
  name: string;
  email?: string;
  phone?: string;
};

export function CustomerListPage() {
  // Persist filters and table state in the URL
  const { params, setParams } = useQueryParams<{
    page?: string;
    size?: string;
    name?: string;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
  }>();

  const page = Number(params.page ?? 1);
  const size = Number(params.size ?? 10);
  const name = params.name ?? '';
  const sortBy = params.sortBy ?? 'name';
  const sortDir = (params.sortDir as 'asc' | 'desc' | undefined) ?? 'asc';

  // Load customers (client-side pagination for now)
  const { data, loading, error, run, setData } = useAsync(
    () => listCustomers({ name: name || undefined }),
    {
      auto: true,
      deps: [name],
      onError: (err) => emitErrorToast({ source: 'CustomerListPage', error: err }),
    },
  );

  // Columns definition for the table
  const { success, error: toastError } = useToast();

  const [deletingId, setDeletingId] = React.useState<number | null>(null);

  const handleDelete = React.useCallback(
    async (row: CustomerRow) => {
      setDeletingId(row.id);
      try {
        // optimistic update
        setData((curr) => (curr ? curr.filter((c: any) => c.id !== row.id) : curr) as any);
        await deleteCustomer(row.id);
        success({ title: 'Deleted', description: `Customer "${row.name}" was deleted.` });
        // refetch to be safe
        await run();
      } catch (err) {
        // rollback by refetching
        await run();
        const appErr = fromAxiosError(err);
        if (appErr.status === 409) {
          toastError({
            title: 'Delete conflict',
            description: `Customer "${row.name}" cannot be deleted due to a conflict.`,
          });
        } else if (appErr.status === 400) {
          toastError({
            title: 'Bad request',
            description: `Delete request for customer "${row.name}" is invalid.`,
          });
        } else {
          toastError({ title: 'Failed to delete', description: appErr.message });
        }
      } finally {
        setDeletingId(null);
      }
    },
    [run, setData, success, toastError],
  );

  const columns = React.useMemo<ColumnDef<CustomerRow>[]>(
    () => [
      { key: 'name', header: 'Name', sortable: true },
      { key: 'email', header: 'Email', sortable: true },
      { key: 'phone', header: 'Phone', sortable: true },
      {
        key: 'actions',
        header: 'Actions',
        cell: (r) => (
          <div className="flex gap-2">
            <Link to={`/customers/${r.id}`} className="text-primary hover:underline text-sm">
              View
            </Link>
            <Link
              to={`/customers/${r.id}/edit`}
              className="text-muted-foreground hover:underline text-sm"
            >
              Edit
            </Link>
            <ConfirmDialog
              title="Delete customer?"
              description={`This will permanently delete "${r.name}".`}
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

  const allRows: CustomerRow[] = React.useMemo(
    () =>
      (data ?? []).map((c) => ({
        id: c.id,
        name: c.name,
        email: c.email,
        phone: c.phone,
      })),
    [data],
  );

  const sortedRows = React.useMemo(() => {
    if (!sortBy) return allRows;
    const copy = [...allRows];
    copy.sort((a: any, b: any) => {
      const av = a[sortBy as keyof CustomerRow];
      const bv = b[sortBy as keyof CustomerRow];
      const astr = av == null ? '' : String(av).toLowerCase();
      const bstr = bv == null ? '' : String(bv).toLowerCase();
      if (astr < bstr) return sortDir === 'asc' ? -1 : 1;
      if (astr > bstr) return sortDir === 'asc' ? 1 : -1;
      return 0;
    });
    return copy;
  }, [allRows, sortBy, sortDir]);

  const total = sortedRows.length;
  const pageStart = (Math.max(1, page) - 1) * Math.max(1, size);
  const pageRows = sortedRows.slice(pageStart, pageStart + Math.max(1, size));

  const onPageChange = (next: number) => setParams({ page: Math.max(1, next) });
  const onSortChange = (key: string, dir: 'asc' | 'desc') =>
    setParams({ sortBy: key, sortDir: dir, page: 1 });

  const onClear = () =>
    setParams({ name: '', page: 1, size: 10, sortBy: 'name', sortDir: 'asc' }, 'replace');

  return (
    <div>
      <div className="flex items-center justify-between mb-4 gap-2">
        <h1 className="text-xl font-semibold">Customers</h1>
        <div className="ml-auto flex items-center gap-2">
          <Input
            placeholder="Filter by name"
            value={name}
            onChange={(e) => setParams({ name: e.target.value, page: 1 }, 'replace')}
            className="w-[240px]"
          />
          <Button variant="outline" size="sm" onClick={onClear}>
            Clear
          </Button>
          <Link
            to="/customers/create"
            className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
          >
            Create Customer
          </Link>
        </div>
      </div>

      {error ? (
        <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          Failed to load customers.
          <Button variant="outline" size="sm" className="ml-3" onClick={() => run()}>
            Retry
          </Button>
        </div>
      ) : null}

      <DataTable<CustomerRow>
        data={pageRows}
        columns={columns}
        loading={loading}
        sortBy={sortBy}
        sortDir={sortDir}
        onSortChange={onSortChange}
        emptyState={
          <EmptyState
            title="No customers yet"
            description="Use the Create button to add your first customer."
          />
        }
        pagination={{ page, pageSize: size, total, onPageChange }}
      />
    </div>
  );
}
