/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * Beer order shipment details
 */
export type BeerOrderShipmentDto = {
    /**
     * Shipment identifier
     */
    id: number;
    /**
     * Identifier of the related beer order
     */
    beerOrderId: number;
    /**
     * Current shipment status
     */
    shipmentStatus: 'PENDING' | 'PACKED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'CANCELLED';
    /**
     * Date and time when the shipment left the warehouse
     */
    shippedDate?: string | null;
    /**
     * Carrier tracking number
     */
    trackingNumber?: string | null;
    /**
     * Carrier name
     */
    carrier?: string | null;
    /**
     * Optional notes about the shipment
     */
    notes?: string | null;
};

