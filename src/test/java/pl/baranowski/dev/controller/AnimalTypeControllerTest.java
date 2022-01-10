package pl.baranowski.dev.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
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
import pl.baranowski.dev.entity.AnimalType;
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

	private List<AnimalType> animalTypesList;

	@BeforeEach
	void setUp() throws Exception {
		animalTypesList = new ArrayList<>();
		animalTypesList.add(new AnimalType(1L, "Kot"));
		animalTypesList.add(new AnimalType(2L, "Wiewiórka"));
		animalTypesList.add(new AnimalType(3L, "Pies"));
	}

	@Test
	void testAddNew_whenValidInput_thenReturns200() throws JsonProcessingException, Exception {
		AnimalTypeDTO dto = new AnimalTypeDTO("Kot");
		
		mockMvc.perform(post("/animalType/new").contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
		.andExpect(status().isOk());
	}
	
	@Test
	void testAddNew_whenNameIsNullOrEmpty_thenReturns400AndErrorDTO() throws JsonProcessingException, Exception {
		AnimalTypeDTO emptyNameDto = new AnimalTypeDTO("");
		AnimalTypeDTO nullNameDto = new AnimalTypeDTO(null);
		ErrorDTO expectedError = new ErrorDTO(HttpStatus.BAD_REQUEST, "name must not be null or empty");

		// when name is empty
		MvcResult emptyResult = mockMvc.perform(post("/animalType/new").contentType("application/json")
				.content(objectMapper.writeValueAsString(emptyNameDto)))
		.andExpect(status().isBadRequest()).andReturn();
		
		// check for exception
		String actualEmptyResponseBody = emptyResult.getResponse().getContentAsString();
		String expectedEmptyResponseBody = objectMapper.writeValueAsString(expectedError);
		assertEquals(StringUtils.trimAllWhitespace(actualEmptyResponseBody), StringUtils.trimAllWhitespace(expectedEmptyResponseBody));

		// when name is null
		MvcResult nullResult = mockMvc.perform(post("/animalType/new").contentType("application/json")
				.content(objectMapper.writeValueAsString(nullNameDto)))
		.andExpect(status().isBadRequest()).andReturn();
		
		// check for exception
		String actualNullResponseBody = nullResult.getResponse().getContentAsString();
		String expectedNullResponseBody = objectMapper.writeValueAsString(expectedError);
		assertEquals(StringUtils.trimAllWhitespace(actualNullResponseBody), StringUtils.trimAllWhitespace(expectedNullResponseBody));
	}
	
	@Test
	void testAddNew_whenValidInput_thenCorrectBusinessCall() throws JsonProcessingException, Exception {
		AnimalTypeDTO dto = new AnimalTypeDTO("Kot");
		
		mockMvc.perform(post("/animalType/new").contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
		.andExpect(status().isOk());
		
		ArgumentCaptor<AnimalTypeDTO> animalTypeCaptor = ArgumentCaptor.forClass(AnimalTypeDTO.class);
		verify(animalTypeService, times(1)).addNew(animalTypeCaptor.capture());
		assertEquals(animalTypeCaptor.getValue().getName(), dto.getName());
	}
	
	@Test
	void testAddNew_whenValidInput_thenReturnsValidAnimalType() throws JsonProcessingException, Exception {
		AnimalTypeDTO dto = new AnimalTypeDTO("Kot");
		AnimalTypeDTO expected = new AnimalTypeDTO(1L, "Kot");

		// mocking service return value
		given(animalTypeService.addNew(dto)).willReturn(expected);
		
		mockMvc.perform(post("/animalType/new").contentType("application/json")
				.content(objectMapper.writeValueAsString(dto)))
				.andDo(mvcResult -> {
					String res = mvcResult.getResponse().getContentAsString(); // problem: empty response
					assertEquals(StringUtils.trimAllWhitespace(objectMapper.writeValueAsString(expected)), StringUtils.trimAllWhitespace(res));
				});
	}
	

	@Test
	void testAddNew_whenNameIsDuplicated_thenReturns400AndErrorDTO() throws JsonProcessingException, Exception {
		AnimalTypeDTO requestDto = new AnimalTypeDTO("Kot");
		ErrorDTO expectedError = new ErrorDTO(HttpStatus.BAD_REQUEST, "this animal type exists in database");

		// mocking service method
		given(animalTypeService.addNew(requestDto)).willThrow(AnimalTypeAllreadyExistsException.class);

		MvcResult result = mockMvc.perform(post("/animalType/new").contentType("application/json")
				.content(objectMapper.writeValueAsString(requestDto))).andReturn();
		// check for exception
		String expectedResponseBody = objectMapper.writeValueAsString(expectedError);
		String actualResponseBody = result.getResponse().getContentAsString();
		assertEquals(StringUtils.trimAllWhitespace(expectedResponseBody), StringUtils.trimAllWhitespace(actualResponseBody));
	}

}
