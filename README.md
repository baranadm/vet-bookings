# vet-bookings

## It is a simple REST API which allows booking an appointments in Veterinary Clinic.

API has been deployed on Heroku: [https://vet-bookings.herokuapp.com/](https://vet-bookings.herokuapp.com/)  
Info: first request takes up approx. 30sec to receive response.


Due to fact, that @Valid validation runs after @RequestBody JSON to DTO conversion, in case Request Body has number type field containing a character, exception thrown by App is InvalidFormatException, not MethodArgumentNotValidException. This causes response error content to contain only this field, not the whole binding result.

For that reason, @RequestBody DTO objects contain String type fields, which are validated by default/custom constraints, and, if needed, are converted to their proper (defined by persistence layer) type.

---
# HTTP Endpoints:
All requests and responses are encoded in UTF-8, every payload has JSON format. Request and response objects are wrapped into DTO's. All errors contain *HttpStatus*, *Error name* and *message*.

#### Animal Type
**@GET /animalTypes/**
- returns List of Animal Types (or empty list)

**@GET /animalTypes/{id}** 
- returns AnimalType - HTTP Status 200
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *AnimalType* with given *id* doesn't exists - HTTP Status 404

**@GET /animalTypes/find**
- params: *name* [String]
- returns List of AnimalTypes with given name (or empty list)
- errors:   
	- if *name* is empty - HTTP Status 400

**@POST /animalTypes/new**
- requires param: *name* [String]
- returns created AnimalType - HTTP Status 201
- errors:  
	- if request body is not valid - HTTP Status 400  
	- if Animal Type *name* is duplicated - HTTP Status 400
	
#### MedSpecialty
**@GET /medSpecialties/all**
- returns List of MedSpecialties (or empty list)

**@GET /medSpecialties/{id}** 
- returns MedSpecialty - HTTP Status 200
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *MedSpecialty* with given *id* doesn't exists - HTTP Status 404
	
**@GET /medSpecialties/find**
- params: *specialty* [String]
- returns List of MedSpecialties with given name (or empty list)
- errors:  
	- if *specialty* is empty - HTTP Status 400

**@POST /medSpecialties/new**
- requires param: *specialty* [String]
- returns created MedSpecialty - HTTP Status 201
- errors:  
	- if request body is not valid - HTTP Status 400
	- if MedSpecialty *name* is duplicated - HTTP Status 400

#### Doctor
**@GET /doctors/{id}** 
- returns DoctorDTO - HTTP Status 200
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *Doctor* with given *id* doesn't exists - HTTP Status 404  

**@GET /doctors/**
- params: *page* - [int], *size* - [int]
- returns all Doctos (or empty Page) - HTTP Status 200
- result type: Page
- errors:  
	 - if *page* or *size* parameter is missing or invalid - HTTP Status 400

**@POST /doctors/**
- requires DoctorDTO body (*name - required, surname - required, hourly rate, NIP*) - JSON
- returns created doctor's DoctorDTO body - HTTP Status 201
- errors:  
	- if request DoctorDTO body is missing/not valid - HTTP Status 400  
	- if NIP is duplicated - HTTP Status 400  
	- if NIP is not valid - HTTP Status 400  
	
**@PUT /doctors/fire/{id}**
- on success - HTTP Status 200  
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *Doctor* with given *id* doesn't exists - HTTP Status 404  
	- if *Doctor* is already inactive - HTTP Status 403
	
**@PUT /doctors/{doctorId}/addAnimalType/{animalTypeId}**
- on success - HTTP Status 200  
- errors:  
	- if any *id* is not valid - HTTP Status 400  
	- if *Doctor* with given *id* doesn't exists - HTTP Status 404  
	- if *AnimalType* with given *id* doesn't exists - HTTP Status 404  
	- if *Doctor* already has given *AnimalType* - HTTP Status 403  
	- if *Doctor* is already inactive - HTTP Status 403
	
**@PUT /doctors/{doctorId}/addMedSpecialty/{medSpecialtyId}**
- behaves like endpoint above

#### Patient
**@GET /patients/{id}** 
- returns PatientDTO - HTTP Status 200
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *Patient* with given *id* doesn't exists - HTTP Status 404  

**@GET /patients/**
- params: *page* - [int], *size* - [int]
- returns all Patients (or empty Page) - HTTP Status 200
- result type: Page
- errors:  
	 - if *page* or *size* parameter is missing or invalid - HTTP Status 400

**@POST /patients/**
- requires NewPatientDTO body (*name - required, animalTypeName - required, ownerName - required, ownerEmail - required, age*) - JSON
- returns created patient's PatientDTO body - HTTP Status 201
- errors:  
	- if request NewPatientDTO body is missing/arguments are not valid - HTTP Status 400  
	- if animalType with *name* has not been found - HTTP Status 404  
	- if *Patient* with exact same arguments has been found in database - HTTP Status 403
	 
#### Visit
**@GET /visits/{id}** 
- returns VisitDTO - HTTP Status 200
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *Visit* with given *id* doesn't exists - HTTP Status 404  

**@GET /visits/**
- params: *page* - [int], *size* - [int]
- returns all Visits (or empty Page) - HTTP Status 200
- result type: Page
- errors:  
	 - if *page* or *size* parameter is missing or invalid - HTTP Status 400

**@POST /visits/**
- requires NewVisitDTO body (*doctorId - required, patientId - required, epoch - required*) - JSON
- returns created visit's VisitDTO body - HTTP Status 201
- errors:  
	- if request NewVisitDTO body is missing/arguments are not valid - HTTP Status 400  
	- if Doctor with *doctorId* has not been found - HTTP Status 404  
	- if Doctor is busy/not available at given epoch - HTTP Status 403  
	- if Doctor is not active - HTTP Status 403  
	- if Patient with *patientId* has not been found - HTTP Status 404  
	- if Patient has another Visit at given epoch - HTTP Status 403  
	- if Doctor does not have Patient's animalType - HTTP Status 403
	- if *epoch* is not in future - HTTP Status 403  

**@GET /visits/check**
- params: *animalTypeName* - [string], *medSpecialtyName* - [string], *epochStart* - [epoch], *epochEnd* - [epoch],
- returns list of available epoch's to book a new Visit for a Doctor with specified AnimalType, MedSpecialty and between given epochs times.
- result type: JSON list
- - errors:  
	- if params are invalid,
	- if AnimalType, MedSpecialty or matching Doctor has not been found,
	- if epochStart is not in the future,
	- if epochEnd is before epochStart
	
