# vet-bookings

## It is a simple REST API which allows booking an appointments in Veterinary Clinic.

Due to fact, that @Valid validation runs after @RequestBody JSON to DTO conversion, in case Request Body has number type field containing a character, exception thrown by App is InvalidFormatException, not MethodArgumentNotValidException. This causes response error content to contain only this field, not the whole binding result.

For that reason, @RequestBody DTO objects contain String type fields, which are validated by default/custom constraints, and, if needed, are converted to their proper (defined by persistence layer) type.

---
## Functionality for AnimalTypes:
- getting list of all animalTypes
- getting one animaltype by id
- finding animalType by name - results with maximum one animalType - duplications are disallowed during addition process
- adding new animalType

	Every AnimalType has:
	- id
	- name

## Functionality for MedSpecialties:
- getting list of all medSpecialties
- getting one medSpecialty by id
- finding medSpecialty by name - results with maximum one animalType - duplications are disallowed during addition process
- adding new medSpecialty

	Every MedSpecialty has:
	- id
	- name

## Functionality for doctors (Vets):
- adding new doctor
- getting one doctor by id
- getting all doctors (with pagination)
- deactivating doctor (soft delete)

	Every Vet has:
	- name
	- surname
	- hourly rate
	- nip (tax-payer id)
	- activity status
	- set of medical specialties (e.g. laryngologist)
	- set of animal types (e.g. squirrels)

## Functionality for pets (Patients):
- adding new patient
- getting one patient by id
- getting all patients (with pagination)

	Every Patient has:
	- name
	- animal type
	- age
	- owner name
	- owner e-mail
	
## Functionality for Visits:
- saving an appointment (not confirmed)
- confirming an appointment
- canceling an appointment
- checking availability of doctor with given Medical Specialty or Animal 

	Every Visit has:
	- doctor
	- patient
	- date and time of appointment
	- confirmation status
---
# HTTP Endpoints:
All requests and responses are encoded in UTF-8, every payload has JSON format. Request and response objects are wrapped into DTO's. All errors contain *HttpStatus*, *Error name* and *message*.

#### Animal Type
**@GET /animalType/all**
- returns List of Animal Types (or empty list)

**@GET /animalType/{id}** 
- returns AnimalType - HTTP Status 200
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *AnimalType* with given *id* doesn't exists - HTTP Status 404

**@GET /animalType/find**
- params: *name* [String]
- returns List of AnimalTypes with given name (or empty list)
	- if *specialty* is empty

**@POST /animalType/new**
- requires AnimalType body (*name*)
- returns created AnimalType - HTTP Status 201
- errors:  
	- if request body is not valid
	- if Animal Type *name* is duplicated
	
#### MedSpecialty
**@GET /medSpecialty/all**
- returns List of MedSpecialties (or empty list)

**@GET /medSpecialty/{id}** 
- returns MedSpecialty - HTTP Status 200
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *MedSpecialty* with given *id* doesn't exists - HTTP Status 404
	
**@GET /medSpecialty/find**
- params: *specialty* [String]
- returns List of MedSpecialties with given name (or empty list)
- errors:  
	- if *specialty* is empty

**@POST /medSpecialty/new**
- requires MedSpecialty body (*name*)
- returns created MedSpecialty - HTTP Status 201
- errors:  
	- if request body is not valid  
	- if MedSpecialty *name* is duplicated

#### Doctor
**@GET /doctor/{id}** 
- returns Vet - HTTP Status 200
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *Vet* with given *id* doesn't exists - HTTP Status 404  

**@GET /doctor/**
- params: *page* - [int], *size* - [int]
- returns all Vets (or empty Page) - HTTP Status 200
- result type: Page
- errors:  
	 - if *page* or *size* parameter is missing or invalid - HTTP Status 400

**@POST /doctor/**
- requires DoctorDTO body (*name - required, surname - required, hourly rate, NIP*)
- returns created doctor's DoctorDTO body - HTTP Status 201
- errors:  
	- if request DoctorDTO body is missing/not valid - HTTP Status 400  
	- if NIP is duplicated - HTTP Status 400  
	- if NIP is not valid - HTTP Status 400  
	
**@PUT /doctor/fire/{id}**
- on success - HTTP Status 200  
- errors:  
	- if *id* is not valid - HTTP Status 400  
	- if *Doctor* with given *id* doesn't exists - HTTP Status 404  
	- if *Doctor* is already inactive - HTTP Status 403
	
**@PUT /doctor/{doctorId}/addAnimalType/{animalTypeId}**
- on success - HTTP Status 200  
- errors:  
	- if any *id* is not valid - HTTP Status 400  
	- if *Doctor* with given *id* doesn't exists - HTTP Status 404  
	- if *AnimalType* with given *id* doesn't exists - HTTP Status 404  
	- if *Doctor* already has given *AnimalType* - HTTP Status 403  
	- if *Doctor* is already inactive - HTTP Status 403
	
**@PUT /doctor/{doctorId}/addMedSpecialty/{medSpecialtyId}**
- behaves like endpoint above

#### Patient

#### Visit

#####to do's:

**/patient**
- everything

**/visit**
- everything