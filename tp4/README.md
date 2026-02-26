

--- Problem 1 : Validation in request params
Summary: Bean Validation Fix Complete
The validation is now working correctly. Here's what was done:
Root Cause
ConstraintViolationException thrown by Hibernate's BeanValidationEventListener at persist time was not being caught by Spring's @RestControllerAdvice. This is by design in Spring Boot 4.x / Spring Framework 7.x - Hibernate validation exceptions bypass Spring's MVC exception handling layer.
Solution Implemented
1. Disabled Hibernate's automatic bean validation at persist time:
      spring.jpa.properties.jakarta.persistence.validation.mode=none
   
2. Added manual validation in service layer using injected jakarta.validation.Validator:
   - OrderService.validate() method now validates entities before save()
   - Throws ConstraintViolationException at service layer where Spring CAN catch it
3. Added HandlerMethodValidationException handler (new in Spring Framework 6.1+):
   - This handles @RequestParam / @PathVariable validation on controllers with @Validated
Test Results
| Endpoint | Test | HTTP Code | Messages |
|----------|------|-----------|----------|
| POST /api/orders?customerName=&customerEmail=invalid | @RequestParam validation | 400 ✅ | French messages |
| POST /api/orders?customerName=John&customerEmail=john@test.com + empty body | Entity validation (empty items) | 400 ✅ | French messages |
| POST /api/products with invalid data | @RequestBody validation | 400 ✅ | French messages |
