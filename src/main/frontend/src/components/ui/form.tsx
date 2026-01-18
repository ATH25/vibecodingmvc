import * as React from 'react';
import {
  Controller,
  type ControllerProps,
  type FieldPath,
  type FieldValues,
  FormProvider,
  useFormContext,
} from 'react-hook-form';

import { cn } from '../../lib/utils';

// Re-export RHF's FormProvider as Form for convenience (shadcn pattern)
export const Form = FormProvider;

// Generic FormField wrapper around RHF Controller (shadcn pattern)
export function FormField<TFieldValues extends FieldValues, TName extends FieldPath<TFieldValues>>(
  props: ControllerProps<TFieldValues, TName>,
) {
  return <Controller {...props} />;
}

// FieldError: show validation error for the given field name
export function FieldError<
  TFieldValues extends FieldValues,
  TName extends FieldPath<TFieldValues>,
>({ name, className }: { name: TName; className?: string }) {
  const {
    formState: { errors },
  } = useFormContext<TFieldValues>();

  // dive safely into nested error objects via the name path
  const error = name
    .toString()
    .split('.')
    .reduce<any>((acc, key) => (acc ? acc[key] : undefined), errors) as
    | { message?: string }
    | undefined;

  if (!error?.message) return null;
  return (
    <p role="alert" className={cn('text-sm text-destructive mt-1', className)}>
      {error.message}
    </p>
  );
}

export default Form;
