package pl.baranowski.dev.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.exception.DoubledSpecialtyException;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.NIPExistsException;
import pl.baranowski.dev.exception.VetNotActiveException;
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
	
	private final VetDTO mostowiak = new VetDTO(1L, "Marek", "Mostówiak", new BigDecimal(150), "1181328620");
	private List<VetDTO> vetsList;
	
	public VetControllerTest() {
		vetsList = new ArrayList<>();
		vetsList.add(new VetDTO("Robert", "Kubica", new BigDecimal(100000), "1213141516"));
		vetsList.add(new VetDTO("Mirosław", "Rosomak", new BigDecimal(100.0), "0987654321"));
		vetsList.add(new VetDTO("Mamadou", "Urghabananandi", new BigDecimal(40.), "5566557755"));
		vetsList.add(new VetDTO("C", "J", new BigDecimal(123.45), "1122334455"));
	}
	
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
		String invalidId = "eee";
		NumberFormatException ex = generateNumberFormatExceptionForString(invalidId);
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
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
	
	
	//@GET 
	//request parameters: page/size - optional, with default values.
	@Test
	void findAll_respondsToRequest() throws Exception {
		Pageable expectedRequest = PageRequest.of(0, 3);
		Page<VetDTO> resultPage = Page.empty(expectedRequest);
		given(vetService.findAll(expectedRequest)).willReturn(resultPage);
		mockMvc.perform(get("/doctor/")
				.param("page", "0")
				.param("size", "3"))
		.andExpect(status().isOk());
	}

	@Test
	void findAll_withPaginationAttr_callsForEntriesWithValidPagination() throws Exception {
		Pageable expectedRequest = PageRequest.of(0, 3);
		Page<VetDTO> resultPage = Page.empty(expectedRequest);
		
		given(vetService.findAll(expectedRequest)).willReturn(resultPage);
		mockMvc.perform(get("/doctor/")
				.param("page", "0")
				.param("size", "3"))
		.andExpect(status().isOk());
		
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(vetService).findAll(pageableCaptor.capture());
		assertEquals(expectedRequest, pageableCaptor.getValue());
	}
	
	@Test
	void findAll_whenNoPaginationAttr_callsForEntriesWithDefaultPagination() throws Exception {
		Pageable expectedDefaultRequest = VetController.DEFAULT_PAGEABLE;
		Page<VetDTO> resultPage = Page.empty(expectedDefaultRequest);
		
		given(vetService.findAll(expectedDefaultRequest)).willReturn(resultPage);
		
		mockMvc.perform(get("/doctor/")
				.param("page", "" + expectedDefaultRequest.getPageNumber())
				.param("size", "" + expectedDefaultRequest.getPageSize()))
		.andExpect(status().isOk());
		
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(vetService).findAll(pageableCaptor.capture());
		assertEquals(expectedDefaultRequest, pageableCaptor.getValue());
	}
	
	@Test
	void findAll_whenPageNumberInvalid_returns400AndError() throws Exception {
		String invalidInt = "bla";
		NumberFormatException ex = generateNumberFormatExceptionForString(invalidInt);
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		MvcResult result = mockMvc.perform(get("/doctor/")
				.param("page", invalidInt)
				.param("size", "1"))
		.andExpect(status().isBadRequest())
		.andReturn();
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void findAll_whenPageSizeInvalid_returns400AndError() throws Exception {
		String invalidInt = "bla";
		NumberFormatException ex = generateNumberFormatExceptionForString(invalidInt);
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		MvcResult result = mockMvc.perform(get("/doctor/")
				.param("page", "2")
				.param("size", invalidInt))
		.andExpect(status().isBadRequest())
		.andReturn();
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void findAll_withPageNumberAndWithoutPageSize_returns400AndError() throws Exception {
		ErrorDTO expected = new ErrorDTO(new EmptyFieldException("size"), HttpStatus.BAD_REQUEST);
		MvcResult result = mockMvc.perform(get("/doctor/")
				.param("page", "1")
				.param("size", ""))
		.andExpect(status().isBadRequest())
		.andReturn();
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void findAll_withoutPageNumberAndWithPageSize_returns400AndError() throws Exception {
		ErrorDTO expected = new ErrorDTO(new EmptyFieldException("page"), HttpStatus.BAD_REQUEST);
		MvcResult result = mockMvc.perform(get("/doctor/")
				.param("page", "")
				.param("size", "1"))
		.andExpect(status().isBadRequest())
		.andReturn();
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void findAll_whenValidInput_returnsVetsPage() throws Exception {
		Pageable pageable = PageRequest.of(0, 3);
		Page<VetDTO> expected = new PageImpl<>(vetsList, pageable, vetsList.size());
		
		given(vetService.findAll(pageable)).willReturn(expected);
		
		MvcResult result = mockMvc.perform(get("/doctor/")
				.param("page", "" + pageable.getPageNumber())
				.param("size", "" + pageable.getPageSize()))
		.andExpect(status().isOk())
		.andReturn();

		assertCorrectJSONResult(expected, result);
	}

	//@POST - should add doctor
	//example body: {"name": "xx", "surname": "xx", "type": "xxx", "animalType": "yyy", "salary": 000, "nip": "xxx"}
	//response: 201 Created
	//response: 400 BAD request. Error handling: duplicated nip, all fields must be not empty, salary cannot be negative.
	@Test
	void addNew_respondsToRequest() throws Exception {
		VetDTO expected = mostowiak;
		mockMvc.perform(post("/doctor/")
				.contentType("application/json")
				.characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsString(expected)))
		.andExpect(status().isCreated());
	}
	
	@Test
	void addNew_whenValidRequestBody_returns201AndEntry() throws JsonProcessingException, Exception {
		VetDTO requestDTO = new VetDTO(0L, mostowiak.getName(), mostowiak.getSurname(), mostowiak.getHourlyRate(), mostowiak.getNip());
		VetDTO expectedDTO = mostowiak;
		given(vetService.addNew(requestDTO)).willReturn(expectedDTO);
		MvcResult result = mockMvc.perform(post("/doctor/")
				.contentType("application/json")
				.characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsString(requestDTO)))
		.andExpect(status().isCreated())
		.andReturn();
		
		assertCorrectJSONResult(expectedDTO, result);
	}
	
	@Test
	void addNew_whenValidRequestAndNIPExists_returns400AndError() throws JsonProcessingException, Exception {
		VetDTO requestDTO = mostowiak;
		ErrorDTO expected = new ErrorDTO(new NIPExistsException(), HttpStatus.BAD_REQUEST);
		
		given(vetService.addNew(requestDTO)).willThrow(new NIPExistsException());
		
		MvcResult result = mockMvc.perform(post("/doctor/")
				.contentType("application/json")
				.characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsString(requestDTO)))
		.andExpect(status().isBadRequest())
		.andReturn();
		
		assertCorrectJSONResult(expected, result);
	}

	@Test
	void addNew_whenAllFieldsNotValid_returns400AndErrorForEveryField() throws JsonProcessingException, Exception {
		VetDTO requestDTO = new VetDTO("", "", new BigDecimal(-1), "1111111112");
		
		mockMvc.perform(post("/doctor/")
				.contentType("application/json")
				.characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsString(requestDTO)))
		.andExpect(status().isBadRequest())
		// checks, if there are 4 errors thrown
		.andExpect(jsonPath("$.fieldErrors", hasSize(4)));
	}
		
	//@PUT /fire/{id}
	//no body
	//response: 200 OK. changed status of given doctor, this doctor will not be able to handle any visits.
	//response: 404 NOT FOUND - if given id not exists in db.

	@Test
	void fire_respondsToRequest() throws Exception {
		mockMvc.perform(put("/doctor/fire/{id}", 1L)
				.characterEncoding("UTF-8"))
		.andExpect(status().isOk());
	}
	
	@Test
	void fire_whenValidId_callsFireWithCorrectId_andReturns200OnSuccess() throws Exception {
		mockMvc.perform(put("/doctor/fire/{id}", 1L)
				.characterEncoding("UTF-8"))
		.andExpect(status().isOk());
		
		ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
		verify(vetService, times(1)).fire(captor.capture());
		assertEquals(1L, captor.getValue());
	}

	@Test
	void fire_whenValidId_callsFireWithCorrectId_andWhenNoEntry_Returns404AndError() throws Exception {
		Long id = 1L;
		String message = "Doctor with id " + id + " not found!";
//		given(vetService.fire(id)).willThrow(new EntityNotFoundException(message));
		doThrow(new EntityNotFoundException(message)).when(vetService).fire(id);
		
		MvcResult result = mockMvc.perform(put("/doctor/fire/{id}", 1L)
				.characterEncoding("UTF-8"))
		.andExpect(status().isNotFound())
		.andReturn();
		
		ErrorDTO expected = new ErrorDTO(new EntityNotFoundException(message), HttpStatus.NOT_FOUND);
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void fire_whenInvalidId_returns400AndError() throws Exception {
		String invalid = "prr";
		MvcResult result = mockMvc.perform(put("/doctor/fire/{id}", invalid)
				.characterEncoding("UTF-8"))
		.andExpect(status().isBadRequest())
		.andReturn();
		
		NumberFormatException ex = generateNumberFormatExceptionForString(invalid);
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		assertCorrectJSONResult(expected, result);
	}

	@Test
	void fire_handlesException() throws Exception {
		String customMessage = "vet id: " + " not found";
		ErrorDTO expected = new ErrorDTO(new VetNotActiveException().withCustomMessage("vet id: " + " not found"), HttpStatus.FORBIDDEN);
		doThrow(new VetNotActiveException().withCustomMessage(customMessage)).when(vetService).fire(mostowiak.getId());
		MvcResult result = mockMvc.perform(put("/doctor/fire/{id}", "1"))
		.andExpect(status().isForbidden())
		.andReturn();
		
		assertCorrectJSONResult(expected, result);
		
	}

	@Test
	void addAnimalType_respondsToRequestAndVerifyBusinessCalls() throws Exception {
		String vetId = "1";
		String atId = "1";
		
		mockMvc.perform(put("/doctor/{id}/addAnimalType/{id}", vetId, atId))
		.andExpect(status().isOk());
		
		ArgumentCaptor<Long> vetIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Long> atIdCaptor = ArgumentCaptor.forClass(Long.class);
		
		verify(vetService, times(1)).addAnimalType(vetIdCaptor.capture(), atIdCaptor.capture());
		
		assertEquals(vetId, vetIdCaptor.getValue().toString());
		assertEquals(atId, atIdCaptor.getValue().toString());
	}
	
	@Test
	void addAnimalType_whenVetIdInvalid_handlesNumberFormatException() throws Exception {
		String invalidId = "p";
		MvcResult result = mockMvc.perform(put("/doctor/{id}/addAnimalType/{id}", invalidId, "1"))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		NumberFormatException ex = generateNumberFormatExceptionForString(invalidId);
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addAnimalType_whenAnimalTypeIdInvalid_handlesNumberFormatException() throws Exception {
		String invalidId = "p";
		MvcResult result = mockMvc.perform(put("/doctor/{id}/addAnimalType/{id}", "1", invalidId))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		NumberFormatException ex = generateNumberFormatExceptionForString(invalidId);
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addAnimalType_handlesEntityNotFoundException() throws Exception {
		EntityNotFoundException ex = new EntityNotFoundException("blah blah blah");
		doThrow(ex).when(vetService).addAnimalType(1L, 1L);

		MvcResult result = mockMvc.perform(put("/doctor/{id}/addAnimalType/{id}", "1", "1"))
				.andExpect(status().isNotFound())
				.andReturn();
		
		
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.NOT_FOUND);
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addAnimalType_handlesDoubledSpecialtyException() throws Exception {
		DoubledSpecialtyException ex = new DoubledSpecialtyException("animalType", "Cows");
		doThrow(ex).when(vetService).addAnimalType(1L, 1L);
		
		MvcResult result = mockMvc.perform(put("/doctor/{id}/addAnimalType/{id}", "1", "1"))
				.andExpect(status().isForbidden())
				.andReturn();
		
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.FORBIDDEN);
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addAnimalType_handlesVetNotActiveException() throws Exception {
		VetNotActiveException ex = new VetNotActiveException();
		doThrow(ex).when(vetService).addAnimalType(1L, 1L);

		MvcResult result = mockMvc.perform(put("/doctor/{id}/addAnimalType/{id}", "1", "1"))
				.andExpect(status().isForbidden())
				.andReturn();

		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.FORBIDDEN);
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addMedSpecialty_respondsToRequestAndVerifyBusinessCalls() throws Exception {
		String vetId = "1";
		String msId = "1";
		
		mockMvc.perform(put("/doctor/{id}/addMedSpecialty/{id}", vetId, msId))
		.andExpect(status().isOk());
		
		ArgumentCaptor<Long> vetIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Long> msIdCaptor = ArgumentCaptor.forClass(Long.class);
		
		verify(vetService, times(1)).addMedSpecialty(vetIdCaptor.capture(), msIdCaptor.capture());
		
		assertEquals(vetId, vetIdCaptor.getValue().toString());
		assertEquals(msId, msIdCaptor.getValue().toString());
	}
	
	@Test
	void addMedSpecialty_whenVetIdInvalid_handlesNumberFormatException() throws Exception {
		String invalidId = "p";
		MvcResult result = mockMvc.perform(put("/doctor/{id}/addMedSpecialty/{id}", invalidId, "1"))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		NumberFormatException ex = generateNumberFormatExceptionForString(invalidId);
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addMedSpecialty_whenMedSpecialtyIdInvalid_handlesNumberFormatException() throws Exception {
		String invalidId = "p";
		MvcResult result = mockMvc.perform(put("/doctor/{id}/addMedSpecialty/{id}", "1", invalidId))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		NumberFormatException ex = generateNumberFormatExceptionForString(invalidId);
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.BAD_REQUEST);
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addMedSpecialty_handlesEntityNotFoundException() throws Exception {
		EntityNotFoundException ex = new EntityNotFoundException("blah blah blah");
		doThrow(ex).when(vetService).addMedSpecialty(1L, 1L);

		MvcResult result = mockMvc.perform(put("/doctor/{id}/addMedSpecialty/{id}", "1", "1"))
				.andExpect(status().isNotFound())
				.andReturn();
		
		
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.NOT_FOUND);
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addMedSpecialty_handlesDoubledSpecialtyException() throws Exception {
		DoubledSpecialtyException ex = new DoubledSpecialtyException("MedSpecialty", "Cows");
		doThrow(ex).when(vetService).addMedSpecialty(1L, 1L);
		
		MvcResult result = mockMvc.perform(put("/doctor/{id}/addMedSpecialty/{id}", "1", "1"))
				.andExpect(status().isForbidden())
				.andReturn();
		
		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.FORBIDDEN);
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addMedSpecialty_handlesVetNotActiveException() throws Exception {
		VetNotActiveException ex = new VetNotActiveException();
		doThrow(ex).when(vetService).addMedSpecialty(1L, 1L);

		MvcResult result = mockMvc.perform(put("/doctor/{id}/addMedSpecialty/{id}", "1", "1"))
				.andExpect(status().isForbidden())
				.andReturn();

		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.FORBIDDEN);
		assertCorrectJSONResult(expected, result);
	}
	
	private void assertCorrectJSONResult(Object expected, MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {
		String expectedTrimmed = StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected));
		String actualTrimmed = StringUtils.trimAllWhitespace(result.getResponse().getContentAsString());

		assertEquals(expectedTrimmed, actualTrimmed);
	}

	private NumberFormatException generateNumberFormatExceptionForString(String invalidValue) {
		NumberFormatException ex = new NumberFormatException();
		try {
			Long.decode(invalidValue);
		} catch (NumberFormatException e) {
			ex = e;
		}
		return ex;
	}
	
}