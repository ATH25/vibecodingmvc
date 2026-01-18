/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BeerRequestDto } from '../models/BeerRequestDto';
import type { BeerResponseDto } from '../models/BeerResponseDto';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class BeersService {
    /**
     * List beers
     * Returns beers with optional filtering and pagination.
     * @param beerName Optional filter to match beers by name (case-insensitive, contains)
     * @param page Zero-based page index (default 0)
     * @param size The size of the page to be returned (default 20)
     * @returns any Page of beers returned
     * @throws ApiError
     */
    public static listBeers(
        beerName?: string,
        page?: number,
        size?: number,
    ): CancelablePromise<{
        content?: Array<BeerResponseDto>;
        totalElements?: number;
        totalPages?: number;
        size?: number;
        number?: number;
    }> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/beers',
            query: {
                'beerName': beerName,
                'page': page,
                'size': size,
            },
            errors: {
                400: `Bad Request`,
            },
        });
    }
    /**
     * Create a beer
     * Creates a new beer and returns the created resource.
     * @param requestBody
     * @returns BeerResponseDto Beer created
     * @throws ApiError
     */
    public static createBeer(
        requestBody: BeerRequestDto,
    ): CancelablePromise<BeerResponseDto> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/beers',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Validation error`,
            },
        });
    }
    /**
     * Get beer by id
     * Returns the beer with the given id if it exists.
     * @param beerId Unique identifier of the beer
     * @returns BeerResponseDto Beer found
     * @throws ApiError
     */
    public static getBeerById(
        beerId: number,
    ): CancelablePromise<BeerResponseDto> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/beers/{beerId}',
            path: {
                'beerId': beerId,
            },
            errors: {
                404: `Beer not found`,
            },
        });
    }
    /**
     * Update a beer
     * Updates an existing beer by id.
     * @param beerId Unique identifier of the beer
     * @param requestBody
     * @returns BeerResponseDto Beer updated
     * @throws ApiError
     */
    public static updateBeer(
        beerId: number,
        requestBody: BeerRequestDto,
    ): CancelablePromise<BeerResponseDto> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/beers/{beerId}',
            path: {
                'beerId': beerId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Validation error`,
                404: `Beer not found`,
            },
        });
    }
    /**
     * Delete a beer
     * Deletes a beer by id. Returns 204 if deleted, 404 if not found.
     * @param beerId Unique identifier of the beer
     * @returns void
     * @throws ApiError
     */
    public static deleteBeer(
        beerId: number,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/beers/{beerId}',
            path: {
                'beerId': beerId,
            },
            errors: {
                404: `Beer not found`,
            },
        });
    }
}
