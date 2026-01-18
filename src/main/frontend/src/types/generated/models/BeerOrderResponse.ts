/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BeerOrderLineResponse } from './BeerOrderLineResponse';
/**
 * Beer order details
 */
export type BeerOrderResponse = {
    /**
     * Order identifier
     */
    id?: number;
    /**
     * Client-provided reference for the order
     */
    customerRef?: string;
    /**
     * Payment amount
     */
    paymentAmount?: number;
    /**
     * Current status of the order
     */
    status?: 'PENDING' | 'PAID' | 'CANCELLED';
    lines?: Array<BeerOrderLineResponse>;
    createdDate?: string;
    updatedDate?: string;
};

