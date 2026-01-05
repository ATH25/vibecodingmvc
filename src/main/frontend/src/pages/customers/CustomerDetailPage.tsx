import React from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

import { Button } from '../../components/ui/button';
import { ConfirmDialog } from '../../components/ui/confirm-dialog';
import useAsync from '../../hooks/useAsync';
import useToast from '../../hooks/useToast';
import { emitErrorToast, fromAxiosError } from '../../lib/errors';
import { deleteCustomer, getCustomer } from '../../services/customerService';

export function CustomerDetailPage() {
  const { id } = useParams();
  const customerId = Number(id);
  const navigate = useNavigate();
  const { success, error: toastError } = useToast();

  const {
    data: customer,
    loading,
    error,
    run,
  } = useAsync(() => getCustomer(customerId), {
    auto: true,
    deps: [customerId],
    onError: (err) => emitErrorToast({ source: 'CustomerDetailPage', error: err }),
  });

  const content = () => {
    if (!id || Number.isNaN(customerId)) {
      return <div className="text-sm text-destructive">Invalid customer id.</div>;
    }
    if (loading) {
      return <div className="text-sm text-muted-foreground">Loading customer…</div>;
    }
    if (error) {
      return (
        <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          Failed to load customer.
          <Button variant="outline" size="sm" className="ml-3" onClick={() => run()}>
            Retry
          </Button>
        </div>
      );
    }
    if (!customer) {
      return <div className="text-sm text-muted-foreground">Customer not found.</div>;
    }

    return (
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-xl font-semibold">{customer.name ?? 'Customer'}</h1>
            <p className="text-sm text-muted-foreground">ID: {customer.id}</p>
          </div>
          <div className="flex gap-2">
            <Link
              to="/customers"
              className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
            >
              Back
            </Link>
            <Link
              to={`/customers/${customer.id}/edit`}
              className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
            >
              Edit
            </Link>
            <ConfirmDialog
              title="Delete customer?"
              description={`This will permanently delete "${customer.name ?? 'this customer'}".`}
              confirmText="Delete"
              confirmVariant="destructive"
              onConfirm={async () => {
                try {
                  await deleteCustomer(customer.id!);
                  success({
                    title: 'Deleted',
                    description: `Customer "${customer.name}" was deleted.`,
                  });
                  navigate('/customers');
                } catch (err) {
                  const appErr = fromAxiosError(err);
                  if (appErr.status === 409) {
                    toastError({
                      title: 'Delete conflict',
                      description: 'Customer cannot be deleted due to a conflict.',
                    });
                  } else if (appErr.status === 400) {
                    toastError({ title: 'Bad request', description: 'Delete request is invalid.' });
                  } else {
                    toastError({ title: 'Failed to delete', description: appErr.message });
                  }
                }
              }}
            >
              <button
                type="button"
                className="inline-flex items-center rounded-md border px-3 py-2 text-sm text-destructive hover:bg-accent/20"
              >
                Delete
              </button>
            </ConfirmDialog>
          </div>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <DetailItem label="Name" value={customer.name} />
          <DetailItem label="Email" value={customer.email} />
          <DetailItem label="Phone" value={customer.phone} />
          <DetailItem label="Address Line 1" value={customer.addressLine1} />
          <DetailItem label="Address Line 2" value={customer.addressLine2} />
          <DetailItem label="City" value={customer.city} />
          <DetailItem label="State" value={customer.state} />
          <DetailItem label="Postal Code" value={customer.postalCode} />
          <DetailItem label="Version" value={customer.version?.toString()} />
          <DetailItem label="Created" value={formatDateTime(customer.createdDate)} />
          <DetailItem label="Updated" value={formatDateTime(customer.updatedDate)} />
        </div>
      </div>
    );
  };

  return <div className="space-y-3">{content()}</div>;
}

function DetailItem({ label, value }: { label: string; value?: string | number | null }) {
  return (
    <div className="space-y-1">
      <div className="text-xs text-muted-foreground">{label}</div>
      <div className="min-h-9 rounded-md border p-3 text-sm">{value ?? '-'}</div>
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
