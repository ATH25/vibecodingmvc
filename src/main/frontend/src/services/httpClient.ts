import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

import { config } from '../lib/config';
import { fromAxiosError } from '../lib/errors';

// Create a shared Axios instance
const http: AxiosInstance = axios.create({
  baseURL: config.apiBaseUrl,
  timeout: 15000,
});

// Request interceptor: default headers
http.interceptors.request.use((req) => {
  req.headers = req.headers ?? {};
  if (!req.headers['Accept']) req.headers['Accept'] = 'application/json';
  if (!req.headers['Content-Type']) req.headers['Content-Type'] = 'application/json';
  return req;
});

// Response interceptor: normalize errors
http.interceptors.response.use(
  (res: AxiosResponse) => res,
  (err) => Promise.reject(fromAxiosError(err)),
);

// Helpers
type RetryOptions = { retry?: number; retryDelayMs?: number };

function sleep(ms: number) {
  return new Promise((r) => setTimeout(r, ms));
}

function shouldRetry(status?: number): boolean {
  // network errors come through with status undefined (handled by axios) and are already mapped
  // Retry on 502/503/504 and general 5xx
  return !status || (status >= 500 && status !== 501);
}

export async function get<T>(
  url: string,
  cfg?: AxiosRequestConfig,
  opts?: RetryOptions,
): Promise<T> {
  const max = Math.max(0, opts?.retry ?? 1);
  const delay = Math.max(0, opts?.retryDelayMs ?? 300);
  let attempt = 0;
  // simple linear backoff
  while (attempt <= max) {
    try {
      const res = await http.get<T>(url, cfg);
      return res.data;
    } catch (e) {
      const appErr = fromAxiosError(e);
      if (attempt < max && shouldRetry(appErr.status)) {
        attempt++;
        if (delay > 0) await sleep(delay * attempt);
        continue;
      }
      throw appErr;
    }
  }
  // This part should technically not be reached because the loop throws or returns
  throw new Error('Retry loop failed');
}

export async function post<T, B = unknown>(
  url: string,
  body?: B,
  cfg?: AxiosRequestConfig,
): Promise<T> {
  try {
    const res = await http.post<T>(url, body, cfg);
    return res.data;
  } catch (e) {
    throw fromAxiosError(e);
  }
}

export async function put<T, B = unknown>(
  url: string,
  body?: B,
  cfg?: AxiosRequestConfig,
): Promise<T> {
  try {
    const res = await http.put<T>(url, body, cfg);
    return res.data;
  } catch (e) {
    throw fromAxiosError(e);
  }
}

export async function patch<T, B = unknown>(
  url: string,
  body?: B,
  cfg?: AxiosRequestConfig,
): Promise<T> {
  try {
    const res = await http.patch<T>(url, body, cfg);
    return res.data;
  } catch (e) {
    throw fromAxiosError(e);
  }
}

export async function del<T>(url: string, cfg?: AxiosRequestConfig): Promise<T> {
  try {
    const res = await http.delete<T>(url, cfg);
    return res.data;
  } catch (e) {
    throw fromAxiosError(e);
  }
}

export { http };
