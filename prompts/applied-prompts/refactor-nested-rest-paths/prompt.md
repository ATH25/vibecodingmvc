Refactor the BeerOrderShipmentController to follow RESTful nested resource path conventions as outlined in guidelines.md.

Objective:
- Update all BeerOrderShipment-related endpoints to use a nested path structure, treating BeerOrderShipment as a sub-resource of BeerOrder.

Instructions:
1. Change the base route to: /api/v1/beerorders/{beerOrderId}/shipments
2. Update all controller mappings:
   - Adjust @RequestMapping, @GetMapping, @PostMapping, @PatchMapping, and @DeleteMapping accordingly.
3. Ensure method parameters include beerOrderId where needed, and route path variables are correctly mapped.
4. Update service method calls to accept beerOrderId if required and maintain correct behavior.
5. Update corresponding OpenAPI YAML files:
   - Location: openapi-starter-main/openapi/paths
   - Ensure path names and parameter descriptions reflect the new structure.
6. Update test class:
   - Modify BeerOrderShipmentControllerTest to use new endpoint paths.
   - Adjust path variables in MockMvc calls accordingly.
7. Preserve:
   - All existing validation logic
   - Error handling behavior
   - Functional output of each endpoint

Completion:
- When all changes are implemented, mark the relevant tasks as complete in prompts/refactor-nested-paths/tasks.md.