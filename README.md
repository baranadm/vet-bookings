# vet-bookings

## It is a simple REST API which allows booking an appointments in Veterinary Clinic.

API has been deployed on Heroku: [https://vet-bookings.herokuapp.com/](https://vet-bookings.herokuapp.com/)  


Due to fact, that @Valid validation runs after @RequestBody JSON to DTO conversion, in case Request Body has number type field containing a character, exception thrown by App is InvalidFormatException, not MethodArgumentNotValidException. This causes response error content to contain only this field, not the whole binding result.

For that reason, @RequestBody DTO objects contain String type fields, which are validated by default/custom constraints, and, if needed, are converted to their proper (defined by persistence layer) type.

---
# HTTP Endpoints:

Please visit https://vet-bookings.herokuapp.com/swagger-ui/index.html to view all available endpoints.

# Solutions/frameworks used:
- A lot of integration tests done in JUnit Jupiter
- Error handling done in @ControllerAdvice
- Custom constraints validation (@Valid)
- Mapping objects: MapStruct
- Database: H2 (tests) / Postgresql (prod)
- Hibernate, JPA