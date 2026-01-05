import { get, post, put, del } from './httpClient';
import type { BeerRequestDto } from '../types/generated/models/BeerRequestDto';
import type { BeerResponseDto } from '../types/generated/models/BeerResponseDto';

export const BEERS_BASE_PATH = '/v1/beers';

export type BeerListParams = {
  page?: number;
  size?: number;
  sort?: string;
  beerName?: string;
  beerStyle?: string;
  upc?: string;
};

export type BeerPage = {
  content: BeerResponseDto[];
  totalElements: number;
  totalPages?: number;
  size: number;
  number: number; // zero-based page index
};

export async function listBeers(params?: BeerListParams): Promise<BeerPage> {
  const query: Record<string, unknown> = {};
  if (params?.beerName) query.beerName = params.beerName;
  if (params?.beerStyle) query.beerStyle = params.beerStyle;
  if (typeof params?.page === 'number') query.page = Math.max(0, params.page - 1); // backend is zero-based
  if (typeof params?.size === 'number') query.size = params.size;
  if (params?.sort) query.sort = params.sort;

  return get<BeerPage>(BEERS_BASE_PATH, { params: query }, { retry: 1, retryDelayMs: 300 });
}

export async function getBeer(id: number): Promise<BeerResponseDto> {
  return get<BeerResponseDto>(`${BEERS_BASE_PATH}/${id}`);
}

export async function createBeer(payload: BeerRequestDto): Promise<BeerResponseDto> {
  return post<BeerResponseDto, BeerRequestDto>(BEERS_BASE_PATH, payload);
}

export async function updateBeer(id: number, payload: BeerRequestDto): Promise<BeerResponseDto> {
  return put<BeerResponseDto, BeerRequestDto>(`${BEERS_BASE_PATH}/${id}`, payload);
}

export async function deleteBeer(id: number): Promise<void> {
  return del<void>(`${BEERS_BASE_PATH}/${id}`);
}
