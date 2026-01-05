import React from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

import { Button } from '../../components/ui/button';
import { ConfirmDialog } from '../../components/ui/confirm-dialog';
import useAsync from '../../hooks/useAsync';
import useToast from '../../hooks/useToast';
import { emitErrorToast, fromAxiosError } from '../../lib/errors';
import { deleteBeer, getBeer } from '../../services/beerService';

export function BeerDetailPage() {
  const { id } = useParams();
  const beerId = Number(id);
  const navigate = useNavigate();
  const { success, error: toastError } = useToast();

  const {
    data: beer,
    loading,
    error,
    run,
  } = useAsync(() => getBeer(beerId), {
    auto: true,
    deps: [beerId],
    onError: (err) => emitErrorToast({ source: 'BeerDetailPage', error: err }),
  });

  const content = () => {
    if (!id || Number.isNaN(beerId)) {
      return <div className="text-sm text-destructive">Invalid beer id.</div>;
    }
    if (loading) {
      return <div className="text-sm text-muted-foreground">Loading beer…</div>;
    }
    if (error) {
      return (
        <div className="rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
          Failed to load beer.
          <Button variant="outline" size="sm" className="ml-3" onClick={() => run()}>
            Retry
          </Button>
        </div>
      );
    }
    if (!beer) {
      return <div className="text-sm text-muted-foreground">Beer not found.</div>;
    }

    return (
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-xl font-semibold">{beer.beerName ?? 'Beer'}</h1>
            <p className="text-sm text-muted-foreground">ID: {beer.id}</p>
          </div>
          <div className="flex gap-2">
            <Link
              to="/beers"
              className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
            >
              Back
            </Link>
            <Link
              to={`/beers/${beer.id}/edit`}
              className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
            >
              Edit
            </Link>
            <ConfirmDialog
              title="Delete beer?"
              description={`This will permanently delete "${beer.beerName ?? 'this beer'}".`}
              confirmText="Delete"
              confirmVariant="destructive"
              onConfirm={async () => {
                try {
                  await deleteBeer(beer.id!);
                  success({
                    title: 'Deleted',
                    description: `Beer "${beer.beerName}" was deleted.`,
                  });
                  navigate('/beers');
                } catch (err) {
                  const appErr = fromAxiosError(err);
                  if (appErr.status === 409) {
                    toastError({
                      title: 'Delete conflict',
                      description: 'Beer cannot be deleted due to a conflict.',
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
          <DetailItem label="Name" value={beer.beerName} />
          <DetailItem label="Style" value={beer.beerStyle} />
          <DetailItem label="UPC" value={beer.upc} />
          <DetailItem
            label="Price"
            value={beer.price != null ? `$${beer.price.toFixed(2)}` : undefined}
          />
          <DetailItem label="Quantity" value={beer.quantityOnHand?.toString()} />
          <DetailItem label="Version" value={beer.version?.toString()} />
          <DetailItem label="Created" value={formatDateTime(beer.createdDate)} />
          <DetailItem label="Updated" value={formatDateTime(beer.updatedDate)} />
          {beer.description ? (
            <div className="sm:col-span-2 lg:col-span-3">
              <div className="text-xs text-muted-foreground mb-1">Description</div>
              <div className="rounded-md border p-3 text-sm">{beer.description}</div>
            </div>
          ) : null}
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
