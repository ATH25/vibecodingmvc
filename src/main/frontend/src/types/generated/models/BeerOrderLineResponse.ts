/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * Beer order line details
 */
export type BeerOrderLineResponse = {
    /**
     * Line identifier
     */
    id?: number;
    /**
     * Beer identifier
     */
    beerId?: number;
    /**
     * Name of the beer
     */
    beerName?: string;
    /**
     * Quantity ordered
     */
    orderQuantity?: number;
    /**
     * Quantity allocated
     */
    quantityAllocated?: number;
    /**
     * Line status
     */
    status?: 'PENDING' | 'ALLOCATED' | 'BACKORDERED' | 'SHIPPED';
};

