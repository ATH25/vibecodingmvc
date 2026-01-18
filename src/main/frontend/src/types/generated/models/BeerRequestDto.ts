/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * Request payload to create or update a beer
 */
export type BeerRequestDto = {
    /**
     * Name of the beer
     */
    beerName: string;
    /**
     * Style of the beer (e.g., ALE, PALE_ALE, IPA)
     */
    beerStyle: string;
    /**
     * Universal Product Code (13-digit)
     */
    upc: string;
    /**
     * Quantity on hand (must be zero or positive)
     */
    quantityOnHand?: number;
    /**
     * Price per unit (must be greater than 0)
     */
    price?: number;
    /**
     * Optional human-readable description of the beer
     */
    description?: string;
};

