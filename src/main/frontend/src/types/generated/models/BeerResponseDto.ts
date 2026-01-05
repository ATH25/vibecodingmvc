/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * Response payload representing a beer
 */
export type BeerResponseDto = {
    /**
     * Unique identifier of the beer
     */
    id?: number;
    /**
     * Entity version for optimistic locking
     */
    version?: number;
    /**
     * Name of the beer
     */
    beerName?: string;
    /**
     * Style of the beer (e.g., ALE, PALE_ALE, IPA)
     */
    beerStyle?: string;
    /**
     * Universal Product Code (13-digit)
     */
    upc?: string;
    /**
     * Quantity on hand
     */
    quantityOnHand?: number;
    /**
     * Price per unit
     */
    price?: number;
    /**
     * Optional human-readable description of the beer
     */
    description?: string;
    /**
     * Creation timestamp
     */
    createdDate?: string;
    /**
     * Last update timestamp
     */
    updatedDate?: string;
};

