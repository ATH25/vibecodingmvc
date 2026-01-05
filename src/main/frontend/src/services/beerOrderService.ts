import { get, post, put, patch, del } from './httpClient';
import type { BeerOrderResponse } from '../types/generated/models/BeerOrderResponse';
import type { BeerOrderShipmentCreateDto } from '../types/generated/models/BeerOrderShipmentCreateDto';
import type { BeerOrderShipmentDto } from '../types/generated/models/BeerOrderShipmentDto';
import type { BeerOrderShipmentUpdateDto } from '../types/generated/models/BeerOrderShipmentUpdateDto';
import type { CreateBeerOrderCommand } from '../types/generated/models/CreateBeerOrderCommand';

export const BEER_ORDERS_BASE_PATH = '/v1/beer-orders';
export const BEER_ORDER_SHIPMENTS_BASE_PATH = '/v1/beerorders'; // Note: no dash based on API spec

export type BeerOrderListParams = {
  page?: number;
  size?: number;
  sort?: string;
  status?: 'PENDING' | 'PAID' | 'CANCELLED';
  customerId?: number;
};

export async function listBeerOrders(params?: BeerOrderListParams): Promise<BeerOrderResponse[]> {
  return get<BeerOrderResponse[]>(
    BEER_ORDERS_BASE_PATH,
    { params },
    { retry: 1, retryDelayMs: 300 },
  );
}

export async function getBeerOrder(id: number): Promise<BeerOrderResponse> {
  return get<BeerOrderResponse>(`${BEER_ORDERS_BASE_PATH}/${id}`);
}

export async function createBeerOrder(payload: CreateBeerOrderCommand): Promise<BeerOrderResponse> {
  return post<BeerOrderResponse, CreateBeerOrderCommand>(BEER_ORDERS_BASE_PATH, payload);
}

export async function updateBeerOrder(
  id: number,
  payload: CreateBeerOrderCommand,
): Promise<BeerOrderResponse> {
  return put<BeerOrderResponse, CreateBeerOrderCommand>(`${BEER_ORDERS_BASE_PATH}/${id}`, payload);
}

export async function deleteBeerOrder(id: number): Promise<void> {
  return del<void>(`${BEER_ORDERS_BASE_PATH}/${id}`);
}

// Shipment actions (nested resource)
export async function listBeerOrderShipments(beerOrderId: number): Promise<BeerOrderShipmentDto[]> {
  return get<BeerOrderShipmentDto[]>(`${BEER_ORDER_SHIPMENTS_BASE_PATH}/${beerOrderId}/shipments`);
}

export async function getBeerOrderShipment(
  beerOrderId: number,
  shipmentId: number,
): Promise<BeerOrderShipmentDto> {
  return get<BeerOrderShipmentDto>(
    `${BEER_ORDER_SHIPMENTS_BASE_PATH}/${beerOrderId}/shipments/${shipmentId}`,
  );
}

export async function createBeerOrderShipment(
  beerOrderId: number,
  payload: BeerOrderShipmentCreateDto,
): Promise<BeerOrderShipmentDto> {
  return post<BeerOrderShipmentDto, BeerOrderShipmentCreateDto>(
    `${BEER_ORDER_SHIPMENTS_BASE_PATH}/${beerOrderId}/shipments`,
    payload,
  );
}

export async function updateBeerOrderShipment(
  beerOrderId: number,
  shipmentId: number,
  payload: BeerOrderShipmentUpdateDto,
): Promise<void> {
  // Using PATCH as per generated service
  return patch<void, BeerOrderShipmentUpdateDto>(
    `${BEER_ORDER_SHIPMENTS_BASE_PATH}/${beerOrderId}/shipments/${shipmentId}`,
    payload,
  );
}

export async function deleteBeerOrderShipment(
  beerOrderId: number,
  shipmentId: number,
): Promise<void> {
  return del<void>(`${BEER_ORDER_SHIPMENTS_BASE_PATH}/${beerOrderId}/shipments/${shipmentId}`);
}
