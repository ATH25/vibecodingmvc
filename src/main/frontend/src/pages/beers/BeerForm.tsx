import React from 'react';
import { useForm } from 'react-hook-form';

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

export type BeerFormValues = {
  beerName: string;
  beerStyle: string;
  upc: string;
  price?: number | '';
  quantityOnHand?: number | '';
  description?: string;
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

type BeerFormProps = {
  initialValues?: Partial<BeerFormValues>;
  onSubmit: (values: BeerFormValues) => void | Promise<void>;
  onCancel?: () => void;
  loading?: boolean;
  submitLabel?: string;
};

export function BeerForm({
  initialValues,
  onSubmit,
  onCancel,
  loading,
  submitLabel = 'Save Beer',
}: BeerFormProps) {
  const form = useForm<BeerFormValues>({
    defaultValues: {
      beerName: '',
      beerStyle: '',
      upc: '',
      price: '',
      quantityOnHand: '',
      description: '',
      ...initialValues,
    },
    mode: 'onBlur',
  });

  const { register, control, handleSubmit } = form;

  return (
    <Form {...form}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6" noValidate>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {/* Name */}
          <div className="space-y-2">
            <Label htmlFor="beerName">Name</Label>
            <Input
              id="beerName"
              placeholder="e.g., Hoppy IPA"
              {...register('beerName', {
                required: 'Name is required',
                minLength: { value: 2, message: 'Name must be at least 2 characters' },
              })}
            />
            <FieldError name="beerName" />
          </div>

          {/* Style */}
          <div className="space-y-2">
            <Label>Style</Label>
            <FormField
              control={control}
              name="beerStyle"
              rules={{ required: 'Style is required' }}
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger>
                    <SelectValue placeholder="Select style" />
                  </SelectTrigger>
                  <SelectContent>
                    {BEER_STYLES.map((s) => (
                      <SelectItem key={s} value={s}>
                        {s.replace('_', ' ')}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />
            <FieldError name="beerStyle" />
          </div>

          {/* UPC */}
          <div className="space-y-2">
            <Label htmlFor="upc">UPC</Label>
            <Input
              id="upc"
              placeholder="13-digit UPC"
              {...register('upc', {
                required: 'UPC is required',
                pattern: { value: /^\d{13}$/g, message: 'UPC must be exactly 13 digits' },
              })}
              inputMode="numeric"
            />
            <FieldError name="upc" />
          </div>

          {/* Price */}
          <div className="space-y-2">
            <Label htmlFor="price">Price ($)</Label>
            <Input
              id="price"
              type="number"
              step="0.01"
              placeholder="0.00"
              {...register('price', {
                min: { value: 0, message: 'Price cannot be negative' },
              })}
            />
            <FieldError name="price" />
          </div>

          {/* Quantity */}
          <div className="space-y-2">
            <Label htmlFor="quantityOnHand">Quantity On Hand</Label>
            <Input
              id="quantityOnHand"
              type="number"
              placeholder="0"
              {...register('quantityOnHand', {
                min: { value: 0, message: 'Quantity cannot be negative' },
              })}
            />
            <FieldError name="quantityOnHand" />
          </div>
        </div>

        {/* Description */}
        <div className="space-y-2">
          <Label htmlFor="description">Description</Label>
          <Input
            id="description"
            placeholder="A short description..."
            {...register('description')}
          />
          <FieldError name="description" />
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
