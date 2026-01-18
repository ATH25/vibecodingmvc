import { zodResolver } from '@hookform/resolvers/zod';
import React from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

import { Button } from '../../components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '../../components/ui/dialog';
import { Form, FormField, FieldError } from '../../components/ui/form';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import useToast from '../../hooks/useToast';
import { notifyError } from '../../lib/errors';
import { createBeerOrderShipment } from '../../services/beerOrderService';

const shipmentSchema = z.object({
  trackingNumber: z.string().min(1, 'Tracking number is required'),
});

export type ShipmentFormValues = z.infer<typeof shipmentSchema>;

type ShipmentDialogProps = {
  orderId: number;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess: () => void;
};

export function ShipmentDialog({ orderId, open, onOpenChange, onSuccess }: ShipmentDialogProps) {
  const { success } = useToast();
  const [loading, setLoading] = React.useState(false);

  const form = useForm<ShipmentFormValues>({
    resolver: zodResolver(shipmentSchema),
    defaultValues: {
      trackingNumber: '',
    },
  });

  const onSubmit = async (values: ShipmentFormValues) => {
    setLoading(true);
    try {
      await createBeerOrderShipment(orderId, {
        trackingNumber: values.trackingNumber,
      });
      success({
        title: 'Shipment created',
        description: `Shipment with tracking #${values.trackingNumber} has been added.`,
      });
      onOpenChange(false);
      onSuccess();
    } catch (err) {
      notifyError(err, { title: 'Failed to create shipment' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Add Shipment for Order #{orderId}</DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              name="trackingNumber"
              render={({ field }) => (
                <div className="space-y-2">
                  <Label htmlFor="trackingNumber">Tracking Number</Label>
                  <Input id="trackingNumber" placeholder="e.g. TRK123456789" {...field} />
                  <FieldError name="trackingNumber" />
                </div>
              )}
            />
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={loading}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? 'Saving...' : 'Create Shipment'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
