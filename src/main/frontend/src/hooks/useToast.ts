import { useMemo } from 'react';

import { useToast as useShadcnToast } from '../components/ui/toast';

export type ToastVariant = 'default' | 'success' | 'error' | 'warning' | 'info';

type BaseOptions = {
  title?: string;
  description?: string;
  duration?: number;
};

export function useToast() {
  const { toast, Toaster } = useShadcnToast();

  const api = useMemo(() => {
    const show = (opts: BaseOptions) => toast(opts);
    const success = (opts: BaseOptions | string) =>
      toast(
        typeof opts === 'string'
          ? { title: 'Success', description: opts }
          : {
              title: opts.title ?? 'Success',
              description: opts.description,
              duration: opts.duration,
            },
      );
    const error = (opts: BaseOptions | string) =>
      toast(
        typeof opts === 'string'
          ? { title: 'Error', description: opts }
          : {
              title: opts.title ?? 'Error',
              description: opts.description,
              duration: opts.duration,
            },
      );
    const info = (opts: BaseOptions | string) =>
      toast(
        typeof opts === 'string'
          ? { title: 'Info', description: opts }
          : { title: opts.title ?? 'Info', description: opts.description, duration: opts.duration },
      );
    return { show, success, error, info };
  }, [toast]);

  return { ...api, Toaster };
}

export default useToast;
