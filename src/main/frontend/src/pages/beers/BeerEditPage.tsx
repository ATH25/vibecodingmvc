import React, { useEffect, useMemo, useRef } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

import { BeerForm, type BeerFormValues } from './BeerForm';
import { Button } from '../../components/ui/button';
import useAsync from '../../hooks/useAsync';
import useToast from '../../hooks/useToast';
import { emitErrorToast, notifyError } from '../../lib/errors';
import { getBeer, updateBeer } from '../../services/beerService';

export function BeerEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { success } = useToast();

  const beerId = useMemo(() => Number(id), [id]);
  const lastCheckedVersion = useRef<number | undefined>(undefined);

  const {
    data: beer,
    loading,
    error,
    run,
    setData,
  } = useAsync(() => getBeer(beerId), {
    auto: !!id && !Number.isNaN(beerId),
    deps: [beerId],
    onError: (err) => emitErrorToast({ source: 'BeerEditPage', error: err }),
  });

  // Track version for concurrency
  useEffect(() => {
    if (beer) {
      lastCheckedVersion.current = beer.version;
    }
  }, [beer]);

  const onSubmit = async (values: BeerFormValues) => {
    if (!id || Number.isNaN(beerId)) return;
    try {
      // Concurrency-friendly: refetch latest before submitting
      const latest = await getBeer(beerId);
      if (latest.version !== lastCheckedVersion.current) {
        // Update data and ask user to review
        setData(latest);
        lastCheckedVersion.current = latest.version;
        emitErrorToast({
          title: 'Beer was updated elsewhere',
          description:
            'We refreshed the form with the latest data. Please review your changes and submit again.',
          error: { code: 'HTTP_409', message: 'Resource modified', status: 409 },
        });
        return;
      }

      const payload = {
        beerName: values.beerName.trim(),
        beerStyle: values.beerStyle,
        upc: values.upc.trim(),
        price: values.price === '' ? undefined : Number(values.price),
        quantityOnHand: values.quantityOnHand === '' ? undefined : Number(values.quantityOnHand),
        description: values.description?.trim() || undefined,
      };

      const updated = await updateBeer(beerId, payload);
      success({
        title: 'Beer updated',
        description: `${updated.beerName ?? 'Beer'} has been saved.`,
      });
      navigate(`/beers/${beerId}`);
    } catch (err) {
      notifyError(err, { title: 'Failed to update beer' });
    }
  };

  const initialValues = useMemo(() => {
    if (!beer) return undefined;
    return {
      beerName: beer.beerName ?? '',
      beerStyle: beer.beerStyle ?? '',
      upc: beer.upc ?? '',
      price: typeof beer.price === 'number' ? beer.price : '',
      quantityOnHand: typeof beer.quantityOnHand === 'number' ? beer.quantityOnHand : '',
      description: beer.description ?? '',
    };
  }, [beer]);

  const content = () => {
    if (!id || Number.isNaN(beerId)) {
      return <div className="text-sm text-destructive">Invalid beer id.</div>;
    }
    if (loading && !beer) {
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
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-semibold tracking-tight">Edit Beer</h1>
          <div className="flex gap-2">
            <Link
              to={`/beers/${beerId}`}
              className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
            >
              Cancel
            </Link>
            <Link
              to="/beers"
              className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
            >
              Back to list
            </Link>
          </div>
        </div>

        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <BeerForm
            key={beer.version} // Re-mount when data changes to reset form
            initialValues={initialValues}
            onSubmit={onSubmit}
            onCancel={() => navigate(`/beers/${beerId}`)}
            loading={loading}
            submitLabel="Save Changes"
          />
        </div>
      </div>
    );
  };

  return <div className="mx-auto max-w-2xl">{content()}</div>;
}
