import { zodResolver } from '@hookform/resolvers/zod';
import { Plus, Trash2 } from 'lucide-react';
import { useForm, useFieldArray } from 'react-hook-form';
import { z } from 'zod';

import { Button } from '../../components/ui/button';
import { Form, FormField, FieldError } from '../../components/ui/form';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui/select';

const orderLineSchema = z.object({
  beerId: z.coerce.number({ required_error: 'Required' }),
  quantity: z.coerce.number().int().positive('Must be positive'),
});

const orderSchema = z.object({
  customerRef: z.string().optional(),
  paymentAmount: z.preprocess(
    (val) => (val === '' ? undefined : Number(val)),
    z.number().min(0, 'Payment cannot be negative').optional(),
  ),
  items: z.array(orderLineSchema).min(1, 'Add at least one item'),
});

export type OrderFormValues = z.infer<typeof orderSchema>;

type OrderFormProps = {
  beers: { id: number; beerName: string }[];
  initialValues?: Partial<OrderFormValues>;
  onSubmit: (values: OrderFormValues) => void | Promise<void>;
  onCancel?: () => void;
  loading?: boolean;
  submitLabel?: string;
};

export function OrderForm({
  beers,
  initialValues,
  onSubmit,
  onCancel,
  loading,
  submitLabel = 'Save Order',
}: OrderFormProps) {
  const form = useForm<OrderFormValues>({
    resolver: zodResolver(orderSchema),
    defaultValues: {
      customerRef: '',
      paymentAmount: undefined,
      items: [{ beerId: undefined as any, quantity: 1 }],
      ...initialValues,
    },
  });

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: 'items',
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
          <FormField
            name="customerRef"
            render={({ field }) => (
              <div className="space-y-2">
                <Label htmlFor="customerRef">Customer Reference</Label>
                <Input id="customerRef" placeholder="e.g. PO-123" {...field} />
                <FieldError name="customerRef" />
              </div>
            )}
          />

          <FormField
            name="paymentAmount"
            render={({ field }) => (
              <div className="space-y-2">
                <Label htmlFor="paymentAmount">Payment Amount ($)</Label>
                <Input
                  id="paymentAmount"
                  type="number"
                  step="0.01"
                  placeholder="0.00"
                  {...field}
                  value={field.value ?? ''}
                />
                <FieldError name="paymentAmount" />
              </div>
            )}
          />
        </div>

        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <Label className="text-base font-semibold">Order Items</Label>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => append({ beerId: undefined as any, quantity: 1 })}
            >
              <Plus className="mr-2 h-4 w-4" />
              Add Item
            </Button>
          </div>
          <FieldError name="items" />

          <div className="space-y-3">
            {fields.map((field, index) => (
              <div key={field.id} className="flex items-end gap-3 rounded-md border p-3 shadow-sm">
                <div className="flex-1 space-y-2">
                  <Label>Beer</Label>
                  <FormField
                    name={`items.${index}.beerId`}
                    render={({ field: beerField }) => (
                      <Select
                        onValueChange={(val) => beerField.onChange(Number(val))}
                        value={beerField.value?.toString()}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="Select beer" />
                        </SelectTrigger>
                        <SelectContent>
                          {beers.map((b) => (
                            <SelectItem key={b.id} value={b.id.toString()}>
                              {b.beerName}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    )}
                  />
                  <FieldError name={`items.${index}.beerId`} />
                </div>

                <div className="w-24 space-y-2">
                  <Label>Quantity</Label>
                  <FormField
                    name={`items.${index}.quantity`}
                    render={({ field: qtyField }) => <Input type="number" {...qtyField} />}
                  />
                  <FieldError name={`items.${index}.quantity`} />
                </div>

                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="text-destructive"
                  onClick={() => remove(index)}
                  disabled={fields.length === 1}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            ))}
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-4 border-t">
          {onCancel && (
            <Button type="button" variant="outline" onClick={onCancel} disabled={loading}>
              Cancel
            </Button>
          )}
          <Button type="submit" disabled={loading}>
            {loading ? 'Saving...' : submitLabel}
          </Button>
        </div>
      </form>
    </Form>
  );
}
