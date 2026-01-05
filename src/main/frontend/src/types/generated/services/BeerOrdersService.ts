/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BeerOrderResponse } from '../models/BeerOrderResponse';
import type { CreateBeerOrderCommand } from '../models/CreateBeerOrderCommand';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class BeerOrdersService {
    /**
     * Create a beer order
     * Creates a new beer order and returns the created resource. The Location header contains the URI of the created order.
     * @param requestBody
     * @returns BeerOrderResponse Created
     * @throws ApiError
     */
    public static create(
        requestBody: CreateBeerOrderCommand,
    ): CancelablePromise<BeerOrderResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/beer-orders',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Bad Request`,
                415: `Unsupported Media Type`,
            },
        });
    }
    /**
     * Get beer order by id
     * Retrieves a beer order by its identifier.
     * @param id ID of the beer order
     * @returns BeerOrderResponse OK
     * @throws ApiError
     */
    public static get(
        id: number,
    ): CancelablePromise<BeerOrderResponse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/beer-orders/{id}',
            path: {
                'id': id,
            },
            errors: {
                404: `Not Found`,
            },
        });
    }
}
