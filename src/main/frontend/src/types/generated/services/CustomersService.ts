/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CustomerRequestDto } from '../models/CustomerRequestDto';
import type { CustomerResponseDto } from '../models/CustomerResponseDto';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class CustomersService {
    /**
     * List customers
     * Returns all customers.
     * @returns CustomerResponseDto List of customers returned
     * @throws ApiError
     */
    public static listCustomers(): CancelablePromise<Array<CustomerResponseDto>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/customers',
            errors: {
                400: `Bad Request`,
            },
        });
    }
    /**
     * Create customer
     * Creates a new customer and returns the created resource.
     * @param requestBody
     * @returns CustomerResponseDto Customer created
     * @throws ApiError
     */
    public static createCustomer(
        requestBody: CustomerRequestDto,
    ): CancelablePromise<CustomerResponseDto> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/customers',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Validation error`,
            },
        });
    }
    /**
     * Get customer by id
     * Returns the customer with the given id if it exists.
     * @param id Unique identifier of the customer
     * @returns CustomerResponseDto Customer found
     * @throws ApiError
     */
    public static getCustomerById(
        id: number,
    ): CancelablePromise<CustomerResponseDto> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/customers/{id}',
            path: {
                'id': id,
            },
            errors: {
                404: `Customer not found`,
            },
        });
    }
    /**
     * Update customer
     * Updates an existing customer by id.
     * @param id Unique identifier of the customer
     * @param requestBody
     * @returns CustomerResponseDto Customer updated
     * @throws ApiError
     */
    public static updateCustomer(
        id: number,
        requestBody: CustomerRequestDto,
    ): CancelablePromise<CustomerResponseDto> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/customers/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Validation error`,
                404: `Customer not found`,
            },
        });
    }
    /**
     * Delete customer
     * Deletes a customer by id. Returns 204 if deleted, 404 if not found.
     * @param id Unique identifier of the customer
     * @returns void
     * @throws ApiError
     */
    public static deleteCustomer(
        id: number,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/customers/{id}',
            path: {
                'id': id,
            },
            errors: {
                404: `Customer not found`,
            },
        });
    }
}
