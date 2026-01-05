/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * Update beer order shipment request
 */
export type BeerOrderShipmentUpdateDto = {
    /**
     * New shipment status
     */
    shipmentStatus?: 'PENDING' | 'PACKED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'CANCELLED';
    /**
     * Shipped date; required when moving to DELIVERED (if not already set)
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

