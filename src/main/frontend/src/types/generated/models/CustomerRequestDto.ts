/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * Request payload to create or update a customer
 */
export type CustomerRequestDto = {
    /**
     * Full name of the customer
     */
    name: string;
    /**
     * Unique email address of the customer
     */
    email: string;
    /**
     * Phone number
     */
    phone?: string;
    /**
     * Address line 1
     */
    addressLine1: string;
    /**
     * Address line 2
     */
    addressLine2?: string;
    /**
     * City
     */
    city?: string;
    /**
     * State or province
     */
    state?: string;
    /**
     * Postal/ZIP code
     */
    postalCode?: string;
};

