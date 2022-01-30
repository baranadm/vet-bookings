package pl.baranowski.dev.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.NewVisitDTO;
import pl.baranowski.dev.dto.PatientDTO;
import pl.baranowski.dev.dto.VetDTO;
import pl.baranowski.dev.dto.VisitDTO;
import pl.baranowski.dev.service.VisitService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = VisitController.class)
class VisitControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	VisitController visitController;
	
	@MockBean
	VisitService visitService;
	
	AnimalTypeDTO animalType = new AnimalTypeDTO(3L, "Wielbłąd");
	VetDTO vet = new VetDTO(1L, "Robert", "Kupicha", "600", "1111111111");
	PatientDTO patient = new PatientDTO(2L, "Maniek", animalType, 8, "Lucyna Brzoza", "brzozazlasuobok@gmail.com");
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void getById_whenValidId_correctCallsAndReturnValue() throws Exception {
		long epoch = System.currentTimeMillis();
		VisitDTO expected = new VisitDTO(1L, vet, patient, epoch, false);
		
		// mocks service return value
		given(visitService.getById(expected.getId())).willReturn(expected);
		
		MvcResult result = mockMvc.perform(get("/visit/{id}", expected.getId())
					.contentType("application/json;charset=UTF-8"))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(status().isOk())
				.andReturn();
		
		// verifies service call
		ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
		verify(visitService, times(1)).getById(idCaptor.capture());
		assertEquals(expected.getId(), idCaptor.getValue());
		
		// verifies JSON result
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void getById_whenValidIdAndEntityNotFound_returns404AndNotFound() throws Exception {long epoch = System.currentTimeMillis();
		VisitDTO notExisting = new VisitDTO(1L, vet, patient, epoch, false);
		EntityNotFoundException exc = new EntityNotFoundException("testing");
		ErrorDTO expected = new ErrorDTO(exc, HttpStatus.NOT_FOUND);
		
		// mocks service return value
		given(visitService.getById(notExisting.getId())).willThrow(exc);
		
		MvcResult result = mockMvc.perform(get("/visit/{id}", notExisting.getId())
					.contentType("application/json;charset=UTF-8"))
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(status().isNotFound())
				.andReturn();
		
		// verifies JSON error response
		assertCorrectJSONResult(expected, result);
	}

	@Test
	void getById_whenInvalidId_returns400AndError() throws Exception {
		MvcResult result = mockMvc.perform(get("/visit/{id}", "a")
				.contentType("application/json;charset=UTF-8"))
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(status().isBadRequest())
			.andReturn();
		
		ErrorDTO expected = new ErrorDTO(new NumberFormatException().getClass().getSimpleName(), "Forinputstring:\"a\"", HttpStatus.BAD_REQUEST);
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void findAll_withPaginationAttr_callsServiceAndReturnsPage() throws Exception {
		// creates mocked result
		List<VisitDTO> visits = Collections.nCopies(3, 
				new VisitDTO(vet, 
						patient, 
						System.currentTimeMillis() + 60*60*24*5, // + 5 days 
						false)); 
		Pageable pageable = PageRequest.of(0, 3);
		Page<VisitDTO> expected = new PageImpl<>(visits, pageable, visits.size());
		
		// mocks service
		given(visitService.findAll(pageable)).willReturn(expected);
		MvcResult result = mockMvc.perform(get("/visit/")
				.param("page", "0")
				.param("size", "3"))
		.andExpect(status().isOk())
		.andReturn();
		
		// verifies business call
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(visitService, times(1)).findAll(pageableCaptor.capture());
		assertEquals(pageable, pageableCaptor.getValue());
		
		// verifies response content
		assertCorrectJSONResult(expected, result);
		
	}
	
	@Test
	void findAll_whenNoPaginationAttr_callsServiceWithDefaultPagination() throws Exception {
		Pageable defaultPaginable = VisitController.DEFAULT_PAGEABLE;
//		Page<VisitDTO> resultPage = Page.empty(defaultPaginable);
		
//		given(visitService.findAll(defaultPaginable)).willReturn(resultPage);
		
		mockMvc.perform(get("/visit/")
				.param("page", "" + defaultPaginable.getPageNumber())
				.param("size", "" + defaultPaginable.getPageSize()))
		.andExpect(status().isOk());
		
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(visitService).findAll(pageableCaptor.capture());
		assertEquals(defaultPaginable, pageableCaptor.getValue());
	}
	
	@Test
	void findAll_paramsInvalidOrEmpty_returns400AndError() throws Exception {
		// PAGE
		mockMvc.perform(get("/visit/")
				.param("page", "a") // invalid value
				.param("size", "1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", Matchers.containsString("page")));
		
		mockMvc.perform(get("/visit/")
				.param("page", "") // empty value
				.param("size", "1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", Matchers.containsString("page")));
		
		mockMvc.perform(get("/visit/")
				.param("page", "-1") // negative value
				.param("size", "1"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", Matchers.containsString("page")));
		

		// SIZE
		mockMvc.perform(get("/visit/")
				.param("page", "0")
				.param("size", "a")) // invalid value
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", Matchers.containsString("size")));
		
		mockMvc.perform(get("/visit/")
				.param("page", "1")
				.param("size", "")) // empty value
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", Matchers.containsString("size")));
		
		mockMvc.perform(get("/visit/")
				.param("page", "12")
				.param("size", "0")) // zero value
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", Matchers.containsString("size")));
		
		mockMvc.perform(get("/visit/")
				.param("page", "12")
				.param("size", "-100")) // negative value
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message", Matchers.containsString("size")));
		
	}

	@Test
	void addNew_whenCorrectInputs_respondsWith201AndCallsServiceCorrectlyAndReturnsDTOs() throws Exception {
		Long epoch = 1643157711000L; //26 January 2022 00:41:51

		NewVisitDTO newVisitDTO = new NewVisitDTO(vet.getId().toString(), patient.getId().toString(), epoch.toString());
		
		VisitDTO expected = new VisitDTO(vet, patient, epoch, false).withId(4L);
		
		// mocking visitService.addNew()
		given(visitService.addNew(vet.getId(), patient.getId(), epoch)).willReturn(expected);
		
		MvcResult result = mockMvc.perform(post("/visit/")
				.content(objectMapper.writeValueAsString(newVisitDTO))
				.contentType("application/json;charset=UTF-8"))
			.andExpect(status().isCreated())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn();
		
		// verifies business call
		ArgumentCaptor<Long> vetIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Long> patientIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Long> epochCaptor = ArgumentCaptor.forClass(Long.class);
		
		verify(visitService, times(1)).addNew(vetIdCaptor.capture(), patientIdCaptor.capture(), epochCaptor.capture());
		assertEquals(vet.getId(), vetIdCaptor.getValue());
		assertEquals(patient.getId(), patientIdCaptor.getValue());
		assertEquals(epoch, epochCaptor.getValue());
		
		// verifies response result
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addNew_validation_handlesIncorrectOrEmptyParams() throws Exception {
		NewVisitDTO incorrect;
		// doctorId = "a"
		incorrect = new NewVisitDTO("a", "1", "1");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "doctorId");

		// doctorId = "" (empty)
		incorrect = new NewVisitDTO("", "1", "1");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "doctorId");

		// doctorId = "-1"
		incorrect = new NewVisitDTO("-1", "1", "1");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "doctorId");

		// doctorId = "1.1"
		incorrect = new NewVisitDTO("1.1", "1", "1");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "doctorId");

		// patientId = "a"
		incorrect = new NewVisitDTO("1", "a", "1");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "patientId");

		// patientId = "" (empty)
		incorrect = new NewVisitDTO("1", "", "1");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "patientId");

		// patientId = "-100"
		incorrect = new NewVisitDTO("1", "-100", "1");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "patientId");

		// epoch = "a"
		incorrect = new NewVisitDTO("1", "100", "a");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "epoch");

		// epoch = "" (empty)
		incorrect = new NewVisitDTO("1", "100", "");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "epoch");

		// epoch = "-2"
		incorrect = new NewVisitDTO("1", "100", "-1");
		mockMvcPerformAndExpect(incorrect, status().isBadRequest(), "epoch");
		
		// null params
		incorrect = new NewVisitDTO(null, null, null);
		mockMvc.perform(post("/visit/")
				.content(objectMapper.writeValueAsString(incorrect))
				.contentType("application/json;charset=UTF-8"))
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$.fieldErrors[*].field", Matchers.hasItems("doctorId")))
		.andExpect(jsonPath("$.fieldErrors[*].field", Matchers.hasItems("patientId")))
		.andExpect(jsonPath("$.fieldErrors[*].field", Matchers.hasItems("epoch")));

	}

	@Test
	void addNew_handlesEntityNotFoundException() throws Exception {
		Long now = System.currentTimeMillis();
		NewVisitDTO newVisit = new NewVisitDTO("1", "2", now.toString());

		given(visitService.addNew(1L, 2L, now)).willThrow(EntityNotFoundException.class);
		
		MvcResult result = mockMvc.perform(post("/visit/")
				.content(objectMapper.writeValueAsString(newVisit))
				.contentType("application/json;charset=UTF-8"))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andReturn();
		
		ErrorDTO expected = new ErrorDTO(new EntityNotFoundException(), HttpStatus.NOT_FOUND);
		
		assertCorrectJSONResult(expected, result);
	}

	private void mockMvcPerformAndExpect(NewVisitDTO requestDTO, ResultMatcher httpStatusMatcher, String field)
			throws Exception, JsonProcessingException {
		mockMvc.perform(post("/visit/")
				.content(objectMapper.writeValueAsString(requestDTO))
				.contentType("application/json;charset=UTF-8"))
		.andExpect(httpStatusMatcher)
		.andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$.fieldErrors[*].field", Matchers.hasItems(field)));
	}

	private void assertCorrectJSONResult(Object expected, MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {
		String expectedTrimmed = StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected));
		String actualTrimmed = StringUtils.trimAllWhitespace(result.getResponse().getContentAsString());

		assertEquals(expectedTrimmed, actualTrimmed);
	}
}