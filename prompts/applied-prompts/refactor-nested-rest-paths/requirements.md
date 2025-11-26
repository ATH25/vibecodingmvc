# Refactor Requirements: Nested REST Paths for BeerOrderShipment

## Goal
Refactor the BeerOrderShipment-related API endpoints to follow proper RESTful design using nested resource paths. This aligns with our guidelines.md best practices.

## Problem Statement
Currently, the BeerOrderShipmentController uses a flat structure like:
  - /api/v1/beerorder-shipments
  - /api/v1/beerorder-shipments/{id}

This violates our guideline:
> Prefer nested resource paths for sub-resources, such as:
> `/api/v1/beerorders/{beerOrderId}/shipments`

## Objectives
- Make REST endpoints more expressive and semantically correct
- Improve discoverability and maintainability
- Align generated OpenAPI and tests with nested structure

## Scope
- Controller: `BeerOrderShipmentController.java`
- Test: `BeerOrderShipmentControllerTest.java`
- OpenAPI YAML: `openapi/paths/*.yaml`
- Possibly affected DTOs and services (parameter updates)

## Out of Scope
- Changing database schema or entity relationships
- Adding new functionality unrelated to routing

## References
- See `guidelines.md` for nested REST path conventions
