package pl.baranowski.dev.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import pl.baranowski.dev.dto.NewPatientDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.entity.AnimalType;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.PatientAllreadyExistsException;
import pl.baranowski.dev.service.PatientService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerTest {

	@MockBean
	PatientService patientService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	PatientDTO patientDTO = new PatientDTO(1L, "Krakers", new AnimalType(1L, "Cat"), 5, "Papa Smurf",
			"papasmurf@gargamel.com");
	NewPatientDTO newPatientDTO = new NewPatientDTO(patientDTO.getName(), patientDTO.getAge(),
			patientDTO.getAnimalType().getName(), patientDTO.getOwnerName(), patientDTO.getOwnerEmail());

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void getById_whenEntityExists_callsCorrectlyAndReturnsDTO() throws Exception {
		given(patientService.getById(patientDTO.getId())).willReturn(patientDTO);
		MvcResult result = mockMvc.perform(get("/patient/{id}", patientDTO.getId()))
				.andExpect(status().isOk())
				.andReturn();
		
		ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
		verify(patientService, times(1)).getById(idCaptor.capture());
		
		// verifies business call
		assertEquals(patientDTO.getId(), idCaptor.getValue());
		assertCorrectJSONResult(patientDTO, result);
	}
	
	@Test
	void getById_whenEntityDoNotExists_throwsEntityNotFoundException() throws Exception {
		EntityNotFoundException expectedException = new EntityNotFoundException("test");
		given(patientService.getById(patientDTO.getId())).willThrow(expectedException);
		MvcResult result = mockMvc.perform(get("/patient/{id}", patientDTO.getId()))
				.andExpect(status().isNotFound())
				.andReturn();
		
		ErrorDTO expectedError = new ErrorDTO(expectedException, HttpStatus.NOT_FOUND);
		
		assertCorrectJSONResult(expectedError, result);
	}
	
	@Test
	void getById_whenInvalidId_throwsNumberFormatException() throws Exception {
		String invalidId = "a";
		NumberFormatException expectedException = generateNumberFormatExceptionForString(invalidId);
		given(patientService.getById(patientDTO.getId())).willThrow(expectedException);
		MvcResult result = mockMvc.perform(get("/patient/{id}", invalidId))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		ErrorDTO expectedError = new ErrorDTO(expectedException, HttpStatus.BAD_REQUEST);
		
		assertCorrectJSONResult(expectedError, result);
	}
	
	@Test
	void findAll_whenValidParams_makesCorrectCallsAndReturns200AndDTOsPage() throws Exception {
		Pageable expectedPageable = PageRequest.of(2, 2);
		Page<PatientDTO> expected = new PageImpl<>(Collections.singletonList(patientDTO), expectedPageable, 1);
		given(patientService.findAll(expectedPageable)).willReturn(expected);

		MvcResult result = mockMvc.perform(get("/patient/")
				.contentType("application/json;charset=UTF-8)")
				.param("page", "2")
				.param("size", "2"))
			.andExpect(status().isOk())
			.andReturn();
		
		// verifies business calls
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(patientService, times(1)).findAll(pageableCaptor.capture());
		assertEquals(expectedPageable, pageableCaptor.getValue());
		
		// verifies response body
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void findAll_whenInvalidParams_handlesException() throws Exception {
		MvcResult resultEmptyPage = mockMvc.perform(get("/patient/")
				.contentType("application/json;charset=UTF-8)")
				.param("page", "")
				.param("size", "2"))
			.andExpect(status().isBadRequest())
			.andReturn();
		EmptyFieldException emptyPageExc = new EmptyFieldException("page");
		ErrorDTO emptyPageError = new ErrorDTO(emptyPageExc, HttpStatus.BAD_REQUEST);
		assertCorrectJSONResult(emptyPageError, resultEmptyPage);

		MvcResult resultEmptySize = mockMvc.perform(get("/patient/")
				.contentType("application/json;charset=UTF-8)")
				.param("page", "2")
				.param("size", ""))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		EmptyFieldException emptySizeExc = new EmptyFieldException("size");
		ErrorDTO emptySizeError = new ErrorDTO(emptySizeExc, HttpStatus.BAD_REQUEST);
		assertCorrectJSONResult(emptySizeError, resultEmptySize);
		
		String invalidValue = "a";
		
		MvcResult resultNumberFormatError = mockMvc.perform(get("/patient/")
				.contentType("application/json;charset=UTF-8)")
				.param("page", "2")
				.param("size", invalidValue))
				.andExpect(status().isBadRequest())
				.andReturn();
		
		NumberFormatException numForExc = generateNumberFormatExceptionForString(invalidValue);
		ErrorDTO numForError = new ErrorDTO(numForExc, HttpStatus.BAD_REQUEST);
		assertCorrectJSONResult(numForError, resultNumberFormatError);
	}
	
	@Test
	void addNew_whenInvalidRequestBody_returns404andErrorDTO() throws JsonProcessingException, Exception {
		NewPatientDTO incorrectNewPatientDTO = new NewPatientDTO("", -1, "", "", "ee");
		
		mockMvc.perform(post("/patient/").content(objectMapper.writeValueAsString(incorrectNewPatientDTO))
				.contentType("application/json;charset=UTF-8")).andExpect(status().isBadRequest())
		// checks, if there are 5 errors thrown
		.andExpect(jsonPath("$.fieldErrors", hasSize(5)));
	}

	@Test
	void addNew_whenValidRequestBody_callsBusinessCorrectlyAndReturns201andNewDTO() throws JsonProcessingException, Exception {
		// mocking patientService return value...
		given(patientService.addNew(newPatientDTO)).willReturn(patientDTO);

		// responds to request, returns 201 on success
		MvcResult result = mockMvc.perform(post("/patient/").content(objectMapper.writeValueAsString(newPatientDTO))
				.contentType("application/json;charset=UTF-8")).andExpect(status().isCreated()).andReturn();

		// correctly calls patientService.addNew()
		ArgumentCaptor<NewPatientDTO> dtoCaptor = ArgumentCaptor.forClass(NewPatientDTO.class);
		verify(patientService, times(1)).addNew(dtoCaptor.capture());
		assertEquals(newPatientDTO, dtoCaptor.getValue());

		// returns correct response body
		assertCorrectJSONResult(patientDTO, result);
	}
	
	@Test
	void addNew_handlesEntityNotFoundException() throws JsonProcessingException, Exception {
		given(patientService.addNew(newPatientDTO)).willThrow(new EntityNotFoundException());

		MvcResult exceptionErrorResult = mockMvc.perform(post("/patient/")
				.content(objectMapper.writeValueAsString(newPatientDTO)).contentType("application/json;charset=UTF-8"))
				.andExpect(status().isNotFound()).andReturn();

		ErrorDTO expected = new ErrorDTO(new EntityNotFoundException(), HttpStatus.NOT_FOUND);
		assertCorrectJSONResult(expected, exceptionErrorResult);
	}
	
	@Test
	void addNew_handlesPatientAllreadyExistsException() throws JsonProcessingException, Exception {
		PatientAllreadyExistsException ex = new PatientAllreadyExistsException("test message");
		given(patientService.addNew(newPatientDTO)).willThrow(ex);

		MvcResult exceptionErrorResult = mockMvc.perform(post("/patient/").content(objectMapper.writeValueAsString(newPatientDTO))
				.contentType("application/json;charset=UTF-8")).andExpect(status().isForbidden()).andReturn();

		ErrorDTO expected = new ErrorDTO(ex, HttpStatus.FORBIDDEN);
		assertCorrectJSONResult(expected, exceptionErrorResult);
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
	
	private void assertCorrectJSONResult(Object expected, MvcResult result)
			throws JsonProcessingException, UnsupportedEncodingException {
		String expectedTrimmed = StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected));
		String actualTrimmed = StringUtils.trimAllWhitespace(result.getResponse().getContentAsString());

		assertEquals(expectedTrimmed, actualTrimmed);
	}

}
