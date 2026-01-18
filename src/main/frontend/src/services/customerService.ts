import { get, post, put, del } from './httpClient';
import type { CustomerRequestDto } from '../types/generated/models/CustomerRequestDto';
import type { CustomerResponseDto } from '../types/generated/models/CustomerResponseDto';

export const CUSTOMERS_BASE_PATH = '/v1/customers';

export type CustomerListParams = {
  page?: number;
  size?: number;
  sort?: string;
  name?: string;
  email?: string;
};

export async function listCustomers(params?: CustomerListParams): Promise<CustomerResponseDto[]> {
  return get<CustomerResponseDto[]>(
    CUSTOMERS_BASE_PATH,
    { params },
    { retry: 1, retryDelayMs: 300 },
  );
}

export async function getCustomer(id: number): Promise<CustomerResponseDto> {
  return get<CustomerResponseDto>(`${CUSTOMERS_BASE_PATH}/${id}`);
}

export async function createCustomer(payload: CustomerRequestDto): Promise<CustomerResponseDto> {
  return post<CustomerResponseDto, CustomerRequestDto>(CUSTOMERS_BASE_PATH, payload);
}

export async function updateCustomer(
  id: number,
  payload: CustomerRequestDto,
): Promise<CustomerResponseDto> {
  return put<CustomerResponseDto, CustomerRequestDto>(`${CUSTOMERS_BASE_PATH}/${id}`, payload);
}

export async function deleteCustomer(id: number): Promise<void> {
  return del<void>(`${CUSTOMERS_BASE_PATH}/${id}`);
}
