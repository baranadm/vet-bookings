package pl.baranowski.dev.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.MedSpecialtyAllreadyExistsException;
import pl.baranowski.dev.service.MedSpecialtyService;

@ExtendWith(SpringExtension.class)
//@WebMvcTest(controllers = MedSpecialtyController.class)
@SpringBootTest
@AutoConfigureMockMvc
class MedSpecialtyControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	MedSpecialtyService medSpecialtyService;
	
	private List<MedSpecialtyDTO> specialtiesDTO = new ArrayList<>();
	
	public MedSpecialtyControllerTest() {
		// some data for testing purposes
		specialtiesDTO.add(new MedSpecialtyDTO("Kardiolog"));
		specialtiesDTO.add(new MedSpecialtyDTO("Chujolog"));
		specialtiesDTO.add(new MedSpecialtyDTO("Pizdolog"));
	}
	@BeforeEach
	void setUp() throws Exception {
	}

	// verify if controller responds for request
	@Test
	void findAll_respondsToRequest() throws Exception {
		mockMvc
			.perform(get("/medSpecialty/all"))
			.andExpect(status().isOk());
	}

	// verify if controller returns correct entries
	@Test
	void findAll_returnsEntries() throws Exception {
		// mock service return value
		given(medSpecialtyService.findAll()).willReturn(specialtiesDTO);

		MvcResult result = mockMvc
			.perform(get("/medSpecialty/all"))
			.andExpect(status().isOk()).andReturn();

		assertCorrectJSONResult(specialtiesDTO, result);
	}

	@Test
	void getById_respondsToRequest() throws Exception {
		mockMvc.perform(get("/medSpecialty/{id}", 1L)).andExpect(status().isOk());
	}

	@Test
	void getById_whenValidInput_verifyBusinessCalls() throws Exception {
		String idString = "123";
		mockMvc.perform(get("/medSpecialty/{id}", "123")).andExpect(status().isOk());

		ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
		verify(medSpecialtyService, times(1)).getById(captor.capture());
		assertEquals(Long.decode(idString), captor.getValue());
	}
	
	@Test
	void getById_whenValidIdAndNoEntry_returns404AndError() {
		assert(false);
	}
	@Test
	void getById_whenValidId_returnsEntry() throws Exception {
		MedSpecialtyDTO expected = new MedSpecialtyDTO(1L, "ĘÓŁĄĆŃŻŻ");
		
		given(medSpecialtyService.getById(1L)).willReturn(expected);
		
		MvcResult result = mockMvc.perform(get("/medSpecialty/{id}", 1L)).andExpect(status().isOk()).andReturn();
		
		assertCorrectJSONResult(expected, result);
	}

	@Test
	void getById_whenInvalidId_returns400AndError() throws Exception {
		ErrorDTO expectedError = new ErrorDTO(new NumberFormatException(), HttpStatus.BAD_REQUEST);
		expectedError.setMessage("digits expected");
		MvcResult result = mockMvc.perform(get("/medSpecialty/{id}", "ł")).andExpect(status().isBadRequest()).andReturn();

		assertCorrectJSONResult(expectedError, result);
	}

	@Test
	void findByName_respondsToRequest() throws Exception {
		MedSpecialtyDTO expected = new MedSpecialtyDTO(1L, "Kóniolog");
		mockMvc.perform(get("/medSpecialty/find").param("specialty", expected.getName())).andExpect(status().isOk());
	}

	@Test
	void findByName_whenValidInput_verifyBusinessCalls() throws Exception {
		String medSpecialtyName = "Kónio log";
		mockMvc.perform(get("/medSpecialty/find").param("specialty", medSpecialtyName)).andExpect(status().isOk());

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(medSpecialtyService, times(1)).findByName(captor.capture());
		assertEquals(medSpecialtyName, captor.getValue());
	}
	
	@Test
	void findByName_whenValidName_returnsCorrectEntries() throws Exception {
		MedSpecialtyDTO expected = new MedSpecialtyDTO(1L, "Kóniolog");
		given(medSpecialtyService.findByName(expected.getName())).willReturn(Collections.singletonList(expected));
		MvcResult result = mockMvc.perform(get("/medSpecialty/find").param("specialty", expected.getName())).andExpect(status().isOk()).andReturn();

		assertCorrectJSONResult(Collections.singletonList(expected), result);
	}

	@Test
	void findByName_whenNameIsEmpty_returns400andError() throws Exception {
		ErrorDTO expectedError = new ErrorDTO(new EmptyFieldException("specialty"), HttpStatus.BAD_REQUEST);
		MvcResult result = mockMvc.perform(get("/medSpecialty/find").param("specialty", "")).andExpect(status().isBadRequest()).andReturn();

		assertCorrectJSONResult(expectedError, result);
	}

	@Test
	void addNew_respondsToRequest() throws JsonProcessingException, Exception {
		MedSpecialtyDTO body = new MedSpecialtyDTO("ĘŁÓ log");
		mockMvc.perform(
				post("/medSpecialty/new")
				.contentType("application/json;charset=UTF-8")
				.content(objectMapper.writeValueAsString(body)))
		.andExpect(status().isOk());
	}
	
	@Test
	void addNew_whenValidInput_verifyBusinessCalls() throws JsonProcessingException, Exception {
		MedSpecialtyDTO dto = new MedSpecialtyDTO("ĘŁÓ log");
		mockMvc.perform(
				post("/medSpecialty/new")
				.contentType("application/json;charset=UTF-8")
				.content(objectMapper.writeValueAsString(dto)))
		.andExpect(status().isOk());
		
		ArgumentCaptor<MedSpecialtyDTO> medSpecialtyCaptor = ArgumentCaptor.forClass(MedSpecialtyDTO.class);
		verify(medSpecialtyService, times(1)).addNew(medSpecialtyCaptor.capture());
		assertEquals(objectMapper.writeValueAsString(medSpecialtyCaptor.getValue()), objectMapper.writeValueAsString(dto));
	}
	
	// fails: returns empty body
	@Test
	void addNew_whenValidInput_returns200AndNewEntry() throws JsonProcessingException, Exception {
		MedSpecialtyDTO dto = new MedSpecialtyDTO("ĘŁÓ log");
		MedSpecialtyDTO expected = new MedSpecialtyDTO(1L, "ĘŁÓ log");

		// mocking service return value
		given(medSpecialtyService.addNew(dto)).willReturn(expected);
		
		mockMvc.perform(
				post("/medSpecialty/new")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
		.andExpect(status().isOk())
				.andDo(mvcResult -> {
					String res = mvcResult.getResponse().getContentAsString();
					assertEquals(StringUtils.trimAllWhitespace(
							objectMapper.writeValueAsString(expected)),
							StringUtils.trimAllWhitespace(res));
				});
		
	}
	
	// fails - return 200 instead of 400 (spring @Valid validator not working)
	@Test
	void addNew_whenEmptyName_returns400AndError() throws JsonProcessingException, Exception {
		MedSpecialtyDTO dto = new MedSpecialtyDTO("");
		ErrorDTO expected = new ErrorDTO("MethodArgumentNotValidException", "specialty must not be null or empty", HttpStatus.BAD_REQUEST);

		MvcResult result = mockMvc.perform(
				post("/medSpecialty/new")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
		.andExpect(status().isBadRequest()).andReturn();
		
		
		assertCorrectJSONResult(expected, result);
	}
	
	@Test
	void addNew_whenDuplicatedName_returns400AndError() throws JsonProcessingException, Exception {
		MedSpecialtyDTO dto = new MedSpecialtyDTO("ĘŁÓ ziom");
		ErrorDTO expected = new ErrorDTO(new MedSpecialtyAllreadyExistsException(), HttpStatus.BAD_REQUEST);
		
		given(medSpecialtyService.addNew(dto))
		.willThrow(MedSpecialtyAllreadyExistsException.class);
		
		MvcResult result = mockMvc.perform(
				post("/medSpecialty/new")
				.contentType("application/json;charset=UTF-8")
				.content(objectMapper.writeValueAsString(dto)))
		.andExpect(status().isBadRequest()).andReturn();
		
		
		assertCorrectJSONResult(expected, result);
		
	}

	private void assertCorrectJSONResult(Object expected, MvcResult result) throws JsonProcessingException, UnsupportedEncodingException {
		String expectedTrimmed = StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected));
		String actualTrimmed = StringUtils.trimAllWhitespace(result.getResponse().getContentAsString());

		assertEquals(expectedTrimmed, actualTrimmed);
	}
	
}