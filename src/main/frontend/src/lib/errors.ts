import type { AxiosError } from 'axios';

// Standardized application error shape used across services/UI
export type AppError = {
  code: string; // machine-friendly code (e.g., HTTP_404, NETWORK_ERROR)
  message: string; // human-friendly message
  status?: number; // HTTP status when applicable
  details?: unknown; // raw payload or extra context
};

export const enum AppErrorCode {
  Unknown = 'UNKNOWN',
  Network = 'NETWORK_ERROR',
}

// RFC 9457 ProblemDetails minimal shape
export type ProblemDetails = {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  code?: string; // non-standard but commonly used
  [key: string]: unknown;
};

export function mapHttpStatusToMessage(status?: number): string {
  switch (status) {
    case 400:
      return 'Bad request. Please check the input and try again.';
    case 401:
      return 'You are not authorized to perform this action.';
    case 403:
      return 'Access forbidden. You do not have permission to proceed.';
    case 404:
      return 'The requested resource was not found.';
    case 409:
      return 'Request could not be completed due to a conflict.';
    case 422:
      return 'The request was well-formed but could not be processed.';
    case 429:
      return 'Too many requests. Please slow down and try again.';
    case 500:
      return 'Server error. Please try again later.';
    case 502:
    case 503:
    case 504:
      return 'Temporary server issue. Please retry shortly.';
    default:
      return 'Unexpected error occurred. Please try again.';
  }
}

export function fromProblemDetails(problem: ProblemDetails, fallbackStatus?: number): AppError {
  const status = problem.status ?? fallbackStatus;
  const code = problem.code || (status ? `HTTP_${status}` : AppErrorCode.Unknown);
  const message = problem.detail || problem.title || mapHttpStatusToMessage(status);
  return {
    code: String(code),
    message,
    status,
    details: problem,
  };
}

export function fromAxiosError(err: AxiosError | unknown): AppError {
  // Network or unknown error first
  // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
  if (!isAxiosError(err)) {
    const anyErr = err as any;
    return {
      code: AppErrorCode.Unknown,
      message: anyErr?.message || 'Unexpected error',
    };
  }

  const ax = err as AxiosError<any>;
  const status = ax.response?.status;
  const data = ax.response?.data as ProblemDetails | undefined;
  if (data && (data.title || data.detail || data.status)) {
    return fromProblemDetails(data, status);
  }

  // Network error (no response)
  if (!status && ax.message && ax.message.toLowerCase().includes('network')) {
    return {
      code: AppErrorCode.Network,
      message: 'Network error. Please check your connection and try again.',
      details: { originalMessage: ax.message },
    };
  }

  const code = (ax as any).code || (status ? `HTTP_${status}` : AppErrorCode.Unknown);
  const message = mapHttpStatusToMessage(status);
  return { code: String(code), message, status, details: ax.response?.data };
}

function isAxiosError(e: unknown): e is AxiosError {
  return typeof e === 'object' && e !== null && (e as any).isAxiosError === true;
}

// Toast integration (UI-agnostic): emit CustomEvent that UI layer can observe and display via shadcn/ui Toaster
export const ErrorToastEventName = 'app:error-toast';

export type ErrorToastEventDetail = {
  title?: string;
  description?: string;
  error: AppError;
};

export function emitErrorToast(detail: ErrorToastEventDetail) {
  if (typeof window !== 'undefined' && typeof window.dispatchEvent === 'function') {
    window.dispatchEvent(new CustomEvent<ErrorToastEventDetail>(ErrorToastEventName, { detail }));
  }
}

export function notifyError(
  err: AppError | unknown,
  options?: { title?: string; fallbackStatus?: number },
) {
  const appErr = isAxiosError(err) ? fromAxiosError(err) : (err as AppError);
  emitErrorToast({
    title: options?.title ?? 'Error',
    description: appErr.message,
    error: appErr,
  });
}
