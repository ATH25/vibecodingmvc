/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * Create beer order shipment request
 */
export type BeerOrderShipmentCreateDto = {
    /**
     * Identifier of the related beer order
     */
    beerOrderId: number;
    /**
     * Desired initial shipment status (defaults to PENDING if omitted)
     */
    shipmentStatus?: 'PENDING' | 'PACKED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'CANCELLED';
    /**
     * Shipped date if known; will be set when status becomes IN_TRANSIT if missing
     */
    shippedDate?: string | null;
    /**
     * Required when status is IN_TRANSIT or later
     */
    trackingNumber?: string | null;
    /**
     * Required when status is IN_TRANSIT or later
     */
    carrier?: string | null;
    /**
     * Optional notes about the shipment
     */
    notes?: string | null;
};

