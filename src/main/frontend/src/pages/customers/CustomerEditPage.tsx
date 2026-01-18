import React, { useEffect, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate, useParams } from 'react-router-dom';

import { Button } from '../../components/ui/button';
import { Form, FieldError } from '../../components/ui/form';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import useAsync from '../../hooks/useAsync';
import useToast from '../../hooks/useToast';
import { emitErrorToast, notifyError } from '../../lib/errors';
import { getCustomer, updateCustomer } from '../../services/customerService';

type FormValues = {
  name: string;
  email: string;
  phone?: string;
  addressLine1: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  postalCode?: string;
};

export function CustomerEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { success } = useToast();

  const customerId = useMemo(() => Number(id), [id]);

  const {
    data: customer,
    loading,
    error,
    run,
  } = useAsync(() => getCustomer(customerId), {
    auto: !!id && !Number.isNaN(customerId),
    deps: [customerId],
    onError: (err) => emitErrorToast({ source: 'CustomerEditPage', error: err }),
  });

  const form = useForm<FormValues>({
    defaultValues: {
      name: '',
      email: '',
      phone: '',
      addressLine1: '',
      addressLine2: '',
      city: '',
      state: '',
      postalCode: '',
    },
    mode: 'onBlur',
  });

  // Prefill form when customer loads
  useEffect(() => {
    if (customer) {
      form.reset({
        name: customer.name ?? '',
        email: customer.email ?? '',
        phone: customer.phone ?? '',
        addressLine1: customer.addressLine1 ?? '',
        addressLine2: customer.addressLine2 ?? '',
        city: customer.city ?? '',
        state: customer.state ?? '',
        postalCode: customer.postalCode ?? '',
      });
    }
  }, [customer, form]);

  const onSubmit = async (values: FormValues) => {
    if (!id || Number.isNaN(customerId)) return;
    try {
      const payload = {
        name: values.name.trim(),
        email: values.email.trim(),
        phone: values.phone?.trim() || undefined,
        addressLine1: values.addressLine1.trim(),
        addressLine2: values.addressLine2?.trim() || undefined,
        city: values.city?.trim() || undefined,
        state: values.state?.trim() || undefined,
        postalCode: values.postalCode?.trim() || undefined,
      };

      const updated = await updateCustomer(customerId, payload);
      success({
        title: 'Customer updated',
        description: `${updated.name ?? 'Customer'} has been saved.`,
      });
      navigate(`/customers/${customerId}`);
    } catch (err) {
      notifyError(err, { title: 'Failed to update customer' });
    }
  };

  const {
    register,
    formState: { isSubmitting },
  } = form;

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
      <>
        <div className="flex items-center justify-between">
          <h1 className="text-xl font-semibold">Edit Customer</h1>
          <div className="flex gap-2">
            <Link
              to={`/customers/${customerId}`}
              className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
            >
              Cancel
            </Link>
          </div>
        </div>

        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3"
            noValidate
          >
            {/* Name */}
            <div>
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                placeholder="Full name"
                {...register('name', {
                  required: 'Name is required',
                  minLength: { value: 2, message: 'Name must be at least 2 characters' },
                })}
              />
              <FieldError<FormValues, 'name'> name={'name'} />
            </div>

            {/* Email */}
            <div>
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="name@example.com"
                {...register('email', {
                  required: 'Email is required',
                  pattern: {
                    value:
                      /^(?:[a-zA-Z0-9_'^&\-]+(?:\.[a-zA-Z0-9_'^&\-]+)*|"(?:[^"\\]|\\.)+")@(?:(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}|\[(?:(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(?:25[0-5]|2[0-4]\d|[01]?\d\d?)\])$/,
                    message: 'Invalid email address',
                  },
                })}
                inputMode="email"
              />
              <FieldError<FormValues, 'email'> name={'email'} />
            </div>

            {/* Phone */}
            <div>
              <Label htmlFor="phone">Phone</Label>
              <Input
                id="phone"
                placeholder="e.g., +1 555-123-4567"
                {...register('phone')}
                inputMode="tel"
              />
            </div>

            {/* Address Line 1 */}
            <div>
              <Label htmlFor="addressLine1">Address line 1</Label>
              <Input
                id="addressLine1"
                placeholder="Street address"
                {...register('addressLine1', { required: 'Address line 1 is required' })}
              />
              <FieldError<FormValues, 'addressLine1'> name={'addressLine1'} />
            </div>

            {/* Address Line 2 */}
            <div>
              <Label htmlFor="addressLine2">Address line 2</Label>
              <Input
                id="addressLine2"
                placeholder="Apartment, suite, etc. (optional)"
                {...register('addressLine2')}
              />
            </div>

            {/* City */}
            <div>
              <Label htmlFor="city">City</Label>
              <Input id="city" placeholder="City" {...register('city')} />
            </div>

            {/* State */}
            <div>
              <Label htmlFor="state">State</Label>
              <Input id="state" placeholder="State/Province" {...register('state')} />
            </div>

            {/* Postal Code */}
            <div>
              <Label htmlFor="postalCode">Postal code</Label>
              <Input
                id="postalCode"
                placeholder="ZIP/Postal code"
                {...register('postalCode')}
                inputMode="numeric"
              />
            </div>

            {/* Actions */}
            <div className="sm:col-span-2 lg:col-span-3 flex items-center gap-2 pt-2">
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Saving…' : 'Save changes'}
              </Button>
              <Link
                to={`/customers/${customerId}`}
                className="inline-flex items-center rounded-md border px-3 py-2 text-sm hover:bg-accent"
              >
                Back
              </Link>
            </div>
          </form>
        </Form>
      </>
    );
  };

  return <div className="space-y-4">{content()}</div>;
}
