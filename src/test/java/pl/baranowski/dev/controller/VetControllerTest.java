package pl.baranowski.dev.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.exception.DatabaseConnectionException;
import pl.baranowski.dev.service.VetService;

//API dla lekarzy
//root path: /doctor
//@POST - should add doctor
//example body: {"name": "xx", "surname": "xx", "type": "xxx", "animalType": "yyy", "salary": 000, "nip": "xxx"}
//response: 201 Created
//response: 400 BAD request. Error handling: duplicated nip, all fields must be not empty, salary cannot be negative.
//
//@GET /{id}
//response: 200 ok. body: {"name": "xx", "surname": "xx", "type": "xxx", "animalType": "yyy", "salary": 000, "nip": "xxx"}
//response: 404 not found - if given id not exists in db.
//
//@GET 
//request parameters: page/size - optional, with default values. Default spring boot pagination expected.
//response: 200 ok: body: page with doctors body content. (nie chce mi sie pisac tutaj:D)
//
//@PUT /fire/{id}
//no body
//response: 200 OK. changed status of given doctor, this doctor will not be able to handle any visits.
//response: 404 NOT FOUND - if given id not exists in db.
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = VetController.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class VetControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	VetService vetService;
	
	private final VetDTO mostowiak = new VetDTO(1L, "Marek", "Mostowiak", 150.0, "1112131415");
	
	@Test // request: @GET /{id}
	void getById_respondsToRequest() throws Exception {
		mockMvc.perform(get("/doctor/{id}", 1L)).andExpect(status().isOk());
	}
	
	@Test
	void getById_whenValidId_returns200AndEntry() throws Exception {
		VetDTO expected = this.mostowiak;
		given(vetService.getById(expected.getId())).willReturn(expected);
		
		MvcResult result = mockMvc.perform(get("/doctor/{id}", expected.getId()))
				.andExpect(status().isOk()).andReturn();
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void getById_whenInvalidId_returns400AndError() throws Exception {
		String invalidId = "łłł";
		ErrorDTO expected = new ErrorDTO(new NumberFormatException(), HttpStatus.BAD_REQUEST);
		expected.setMessage("digits expected");
		MvcResult result = mockMvc.perform(get("/doctor/{id}", invalidId))
				.andExpect(status().isBadRequest()).andReturn();
		
		assertCorrectJSONResult(expected, result);
		
	}
	
	@Test
	void getById_whenValidIdAndNoEntry_returns404AndError() throws Exception {
		ErrorDTO expected = new ErrorDTO(new EntityNotFoundException(), HttpStatus.NOT_FOUND);
		given(vetService.getById(1L)).willThrow(EntityNotFoundException.class);
		
		MvcResult result = mockMvc.perform(get("/doctor/{id}", 1L))
				.andExpect(status().isNotFound()).andReturn();
		
		assertCorrectJSONResult(expected, result);
	}
	
	
	// coś zjebane...
	// trzeba dopisać do handlera i zasymulować niedziałającą bazę - chyba będzie ciężko
	// może olać jak na razie?
	@Test
	void getById_whenNoDBAccess_returns500AndError() throws Exception {
		ErrorDTO expected = new ErrorDTO(new DatabaseConnectionException(), HttpStatus.INTERNAL_SERVER_ERROR);
		
		given(vetService.getById(1L)).willThrow(DatabaseConnectionException.class);
		MvcResult result = mockMvc.perform(get("/doctor/get/{id}", 1L)).andExpect(status().isInternalServerError()).andReturn();
		assertCorrectJSONResult(expected, result);
	}
	
	//@GET 
	//request parameters: page/size - optional, with default values.
	@Test
	void findAll_respondsToRequest() {
		assert(false);
	}
	
	@Test
	void findAll_whenNoPaginationAttr_callsForEntriesWithDefaultPagination() {
		assert(false);
	}
	
	@Test
	void findAll_withPaginationAttr_callsForEntriesWithValidPagination() {
		assert(false);
	}
	
	@Test
	void findAll_whenPaginationInvalid_returns400AndError() {
		assert(false);
	}
	
	@Test
	void findAll_whenNoDBAccess_returns500AndError() {
		assert(false);
	}
	
	//@POST - should add doctor
	//example body: {"name": "xx", "surname": "xx", "type": "xxx", "animalType": "yyy", "salary": 000, "nip": "xxx"}
	//response: 201 Created
	//response: 400 BAD request. Error handling: duplicated nip, all fields must be not empty, salary cannot be negative.
	
	@Test
	void addNew_respondsToRequest() {
		assert(false);
	}
	
	@Test
	void addNew_whenValidRequestBody_returns201AndEntry() {
		assert(false);
	}
	
	@Test
	void addNew_whenValidRequestAndNIPExists_returns400AndError() {
		assert(false);
	}
	
	@Test
	void addNew_whenValidRequestBodyAndAnimalTypeNotExists_returns400AndError() {
		assert(false);
	}
	
	@Test
	void addNew_whenValidRequestBodyAndMedSpecialityNotExists_returns400AndError() {
		assert(false);
	}

	@Test
	void addNew_whenEmptyField_returns400AndError() {
		assert(false);
	}
	
	@Test
	void addNew_whenNegativeSalary_returns400AndError() {
		assert(false);
	}
	
	@Test
	void addNew_whenIncorrectNIP_returns400AndError() {
		assert(false);
	}
	
	@Test
	void addNew_whenNoDBAccess_returns500AndError() {
		assert(false);
	}
	
	//@PUT /fire/{id}
	//no body
	//response: 200 OK. changed status of given doctor, this doctor will not be able to handle any visits.
	//response: 404 NOT FOUND - if given id not exists in db.

	@Test
	void fire_respondsToRequest() {
		assert(false);
	}
	
	@Test
	void fire_whenValidId_callsFireWithCorrectId_andReturns200OnSuccess() {
		assert(false);
	}
	
	@Test
	void fire_whenValidId_callsFireWithCorrectId_andWhenNoEntry_Returns404AndError() {
		assert(false);
	}
	
	@Test
	void fire_whenInvalidId_returns400AndError() {
		assert(false);
	}
	
	@Test
	void fire_whenNoDBAccess_returns500AndError() {
		assert(false);
	}
	
	private void assertCorrectJSONResult(Object expected, MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {
		String expectedTrimmed = StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected));
		String actualTrimmed = StringUtils.trimAllWhitespace(result.getResponse().getContentAsString());

		assertEquals(expectedTrimmed, actualTrimmed);
	}
}