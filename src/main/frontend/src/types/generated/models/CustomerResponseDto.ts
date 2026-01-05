/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * Response payload representing a customer
 */
export type CustomerResponseDto = {
    /**
     * Unique identifier of the customer
     */
    id: number;
    /**
     * Entity version for optimistic locking
     */
    version: number;
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
    /**
     * Creation timestamp
     */
    createdDate: string;
    /**
     * Last update timestamp
     */
    updatedDate: string;
};

