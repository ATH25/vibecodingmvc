import * as React from 'react';

import { Button, type ButtonProps } from './button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from './dialog';

export type ConfirmDialogProps = {
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
  title?: React.ReactNode;
  description?: React.ReactNode;
  confirmText?: string;
  cancelText?: string;
  confirmVariant?: ButtonProps['variant'];
  onConfirm?: () => void | Promise<void>;
  children?: React.ReactNode; // optional trigger
  disabled?: boolean;
};

export function ConfirmDialog({
  open,
  onOpenChange,
  title = 'Are you sure?',
  description = 'This action cannot be undone.',
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  confirmVariant = 'destructive',
  onConfirm,
  children,
  disabled,
}: ConfirmDialogProps) {
  const [internalOpen, setInternalOpen] = React.useState(false);
  const [loading, setLoading] = React.useState(false);

  const isControlled = typeof open === 'boolean';
  const isOpen = isControlled ? !!open : internalOpen;

  const setOpen = (v: boolean) => {
    if (disabled) return;
    if (isControlled) onOpenChange?.(v);
    else setInternalOpen(v);
  };

  const handleConfirm = async () => {
    if (!onConfirm) {
      setOpen(false);
      return;
    }
    try {
      setLoading(true);
      await onConfirm();
      setOpen(false);
    } finally {
      setLoading(false);
    }
  };

  const content = (
    <DialogContent>
      <DialogHeader>
        <DialogTitle>{title}</DialogTitle>
        {description ? <DialogDescription>{description}</DialogDescription> : null}
      </DialogHeader>
      <DialogFooter>
        <Button variant="outline" onClick={() => setOpen(false)} disabled={loading}>
          {cancelText}
        </Button>
        <Button variant={confirmVariant} onClick={handleConfirm} disabled={loading}>
          {loading ? 'Working…' : confirmText}
        </Button>
      </DialogFooter>
    </DialogContent>
  );

  return (
    <Dialog open={isOpen} onOpenChange={setOpen}>
      {children ? <DialogTrigger asChild>{children}</DialogTrigger> : null}
      {content}
    </Dialog>
  );
}
