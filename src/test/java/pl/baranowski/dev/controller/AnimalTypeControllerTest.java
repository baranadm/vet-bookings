package pl.baranowski.dev.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.exception.AnimalTypeAllreadyExistsException;
import pl.baranowski.dev.service.AnimalTypeService;

@ExtendWith(SpringExtension.class)
//@WebMvcTest(controllers = AnimalTypeController.class)
@SpringBootTest
@AutoConfigureMockMvc
class AnimalTypeControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@MockBean
	AnimalTypeService animalTypeService;

	private List<AnimalTypeDTO> animalTypesDTOList;

	@BeforeEach
	void setUp() throws Exception {
		animalTypesDTOList = new ArrayList<>();
		animalTypesDTOList.add(new AnimalTypeDTO(1L, "Kot"));
		animalTypesDTOList.add(new AnimalTypeDTO(2L, "Wiewiórka"));
		animalTypesDTOList.add(new AnimalTypeDTO(3L, "Pies"));
	}

	
	@Test
	void testFindAll_respondsToRequest() throws Exception {
		given(animalTypeService.findAll()).willReturn(animalTypesDTOList);
		mockMvc.perform(get("/animalType/all")).andExpect(status().isOk());
	}

	@Test
	void testFindAll_returnsEntries() throws Exception {
		given(animalTypeService.findAll()).willReturn(animalTypesDTOList);
		
		MvcResult result = mockMvc.perform(get("/animalType/all")).andExpect(status().isOk()).andReturn();

		String expectedTrimmed = StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(animalTypesDTOList));
		String actualTrimmed = StringUtils.trimAllWhitespace(result.getResponse().getContentAsString());

		assertEquals(expectedTrimmed, 
				actualTrimmed);
	}

	
	@Test
	void testFindById_whenValidId_respondsToRequest() throws Exception {
		given(animalTypeService.findById(1L)).willReturn(new AnimalTypeDTO());
		mockMvc.perform(get("/animalType/{id}", 1L)).andExpect(status().isOk());
	}
	
	@Test
	void testFindById_whenValidId_returnsCorrectEntry() throws Exception {
		AnimalTypeDTO expected = new AnimalTypeDTO(1L, "Wiewiórka");
		given(animalTypeService.findById(1L)).willReturn(expected);
		
		MvcResult result = mockMvc.perform(get("/animalType/{id}", 1L)).andExpect(status().isOk()).andReturn();

		String expectedTrimmed = StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected));
		String actualTrimmed = StringUtils.trimAllWhitespace(result.getResponse().getContentAsString());

		assertEquals(expectedTrimmed, actualTrimmed);
	}
	
	@Test
	void testFindById_whenInvalidId_returns400AndThrowsException() throws Exception {
		mockMvc.perform(get("/animalType/{id}", "aaa")).andExpect(status().isBadRequest()).andReturn();
	}

	
	@Test
	void testFindByName_respondsToRequest() throws Exception {
		mockMvc.perform(get("/animalType/find").param("name", "Wiewiórka")).andExpect(status().isOk());
	}

	// fails - returns json wrapped in [] (result = [{}]), expects only json ({})
	@Test
	void testFindByName_returnsEntries() throws Exception {
		AnimalTypeDTO expected = new AnimalTypeDTO(1L, "Wiewiórka");
		given(animalTypeService.findByName(expected.getName())).willReturn(Collections.singletonList(expected));
		MvcResult result = mockMvc.perform(get("/animalType/find").param("name", "Wiewiórka")).andExpect(status().isOk()).andReturn();
		
		String expectedTrimmed = StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected));
		String actualTrimmed = StringUtils.trimAllWhitespace(result.getResponse().getContentAsString());

		assertEquals(expectedTrimmed, actualTrimmed);
	}
	
	
	@Test
	void testAddNew_whenValidInput_thenReturns200() throws JsonProcessingException, Exception {
		AnimalTypeDTO dto = new AnimalTypeDTO("Wiewiórka");
		mockMvc.perform(
				post("/animalType/new").contentType("application/json").content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());
	}

	@Test
	void testAddNew_whenValidInput_thenCorrectBusinessCall() throws JsonProcessingException, Exception {
		AnimalTypeDTO dto = new AnimalTypeDTO("Wiewiórka");

		mockMvc.perform(
				post("/animalType/new").contentType("application/json").content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isOk());

		ArgumentCaptor<AnimalTypeDTO> animalTypeCaptor = ArgumentCaptor.forClass(AnimalTypeDTO.class);
		verify(animalTypeService, times(1)).addNew(animalTypeCaptor.capture());
		assertEquals(animalTypeCaptor.getValue().getName(), dto.getName());
	}

	@Test
	void testAddNew_whenValidInput_thenReturnsValidAnimalType() throws JsonProcessingException, Exception {
		AnimalTypeDTO dto = new AnimalTypeDTO("Wiewiórka");
		AnimalTypeDTO expected = new AnimalTypeDTO(1L, "Wiewiórka");

		// mocking service return value
		given(animalTypeService.addNew(dto)).willReturn(expected);

		mockMvc.perform(
				post("/animalType/new").contentType("application/json").content(objectMapper.writeValueAsString(dto)))
				.andDo(mvcResult -> {
					String res = mvcResult.getResponse().getContentAsString();
					assertEquals(StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected)),
							StringUtils.trimAllWhitespace(res));
				});
	}
	
	@Test
	void testAddNew_whenNameIsEmpty_thenReturns400AndErrorDTO() throws JsonProcessingException, Exception {
		AnimalTypeDTO emptyNameDto = new AnimalTypeDTO("");
		ErrorDTO expectedError = new ErrorDTO("MethodArgumentNotValidException", "name must not be null or empty", HttpStatus.BAD_REQUEST);
		
		// when name is empty
		MvcResult emptyResult = mockMvc
				.perform(post("/animalType/new").contentType("application/json")
						.content(objectMapper.writeValueAsString(emptyNameDto)))
				.andExpect(status().isBadRequest()).andReturn();
		
		// check for exception
		String actualEmptyResponseBody = emptyResult.getResponse().getContentAsString();
		String expectedEmptyResponseBody = objectMapper.writeValueAsString(expectedError);
		assertEquals(StringUtils.trimAllWhitespace(actualEmptyResponseBody),
				StringUtils.trimAllWhitespace(expectedEmptyResponseBody));
	}

	@Test
	void testAddNew_whenNameIsDuplicated_thenReturns400AndErrorDTO() throws JsonProcessingException, Exception {
		AnimalTypeDTO requestDto = new AnimalTypeDTO("Wiewiórka");
		ErrorDTO expectedError = new ErrorDTO(new AnimalTypeAllreadyExistsException(), HttpStatus.BAD_REQUEST);

		// mocking service method
		given(animalTypeService.addNew(requestDto)).willThrow(AnimalTypeAllreadyExistsException.class);

		MvcResult result = mockMvc.perform(post("/animalType/new").contentType("application/json")
				.content(objectMapper.writeValueAsString(requestDto))).andReturn();
		// check for exception
		String expectedResponseBody = objectMapper.writeValueAsString(expectedError);
		String actualResponseBody = result.getResponse().getContentAsString();
		assertEquals(StringUtils.trimAllWhitespace(expectedResponseBody),
				StringUtils.trimAllWhitespace(actualResponseBody));
	}

}
