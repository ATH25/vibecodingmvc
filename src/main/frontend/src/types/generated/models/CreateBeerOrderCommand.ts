/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CreateBeerOrderItem } from './CreateBeerOrderItem';
/**
 * Command to create a beer order
 */
export type CreateBeerOrderCommand = {
    /**
     * Client-provided reference for the order
     */
    customerRef?: string;
    /**
     * Payment amount for the order
     */
    paymentAmount: number;
    /**
     * Order items
     */
    items: Array<CreateBeerOrderItem>;
};

