/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BeerOrderShipmentCreateDto } from '../models/BeerOrderShipmentCreateDto';
import type { BeerOrderShipmentDto } from '../models/BeerOrderShipmentDto';
import type { BeerOrderShipmentUpdateDto } from '../models/BeerOrderShipmentUpdateDto';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class BeerOrderShipmentsService {
    /**
     * Create a beer order shipment for a beer order
     * @param beerOrderId
     * @param requestBody
     * @returns BeerOrderShipmentDto Created
     * @throws ApiError
     */
    public static createBeerOrderShipment(
        beerOrderId: number,
        requestBody: BeerOrderShipmentCreateDto,
    ): CancelablePromise<BeerOrderShipmentDto> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/beerorders/{beerOrderId}/shipments',
            path: {
                'beerOrderId': beerOrderId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Bad Request`,
                404: `Beer order not found`,
            },
        });
    }
    /**
     * List shipments for a beer order
     * @param beerOrderId
     * @returns BeerOrderShipmentDto OK
     * @throws ApiError
     */
    public static listBeerOrderShipments(
        beerOrderId: number,
    ): CancelablePromise<Array<BeerOrderShipmentDto>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/beerorders/{beerOrderId}/shipments',
            path: {
                'beerOrderId': beerOrderId,
            },
            errors: {
                400: `Bad Request`,
            },
        });
    }
    /**
     * Get a beer order shipment by id for a beer order
     * @param beerOrderId
     * @param id
     * @returns BeerOrderShipmentDto OK
     * @throws ApiError
     */
    public static getBeerOrderShipment(
        beerOrderId: number,
        id: number,
    ): CancelablePromise<BeerOrderShipmentDto> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/beerorders/{beerOrderId}/shipments/{id}',
            path: {
                'beerOrderId': beerOrderId,
                'id': id,
            },
            errors: {
                404: `Not Found`,
            },
        });
    }
    /**
     * Update a beer order shipment
     * @param beerOrderId
     * @param id
     * @param requestBody
     * @returns void
     * @throws ApiError
     */
    public static updateBeerOrderShipment(
        beerOrderId: number,
        id: number,
        requestBody: BeerOrderShipmentUpdateDto,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'PATCH',
            url: '/api/v1/beerorders/{beerOrderId}/shipments/{id}',
            path: {
                'beerOrderId': beerOrderId,
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Bad Request`,
                404: `Not Found`,
            },
        });
    }
    /**
     * Delete a beer order shipment
     * @param beerOrderId
     * @param id
     * @returns void
     * @throws ApiError
     */
    public static deleteBeerOrderShipment(
        beerOrderId: number,
        id: number,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/beerorders/{beerOrderId}/shipments/{id}',
            path: {
                'beerOrderId': beerOrderId,
                'id': id,
            },
            errors: {
                404: `Not Found`,
            },
        });
    }
}
