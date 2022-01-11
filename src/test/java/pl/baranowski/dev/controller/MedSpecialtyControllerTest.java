package pl.baranowski.dev.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.baranowski.dev.entity.MedSpecialty;
import pl.baranowski.dev.service.MedSpecialtyService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MedSpecialtyController.class)
class MedSpecialtyControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	MedSpecialtyService medSpecialtyService;
	
	private List<MedSpecialty> specialties = new ArrayList<>();
	@BeforeEach
	void setUp() throws Exception {
		// some data for testing purposes
		specialties.add(new MedSpecialty("Kardiolog"));
		specialties.add(new MedSpecialty("Chujolog"));
		specialties.add(new MedSpecialty("Pizdolog"));
	}

	// verify if controller responds for request
	@Test
	void findAll_respondToRequest() throws Exception {
		mockMvc
			.perform(get("/medSpecialty/all"))
			.andExpect(status().isOk());
	}
	
	// verify if controller returns correct entries
	@Test
	void findAll_returnsEntries() throws Exception {
		// mock service return value
		given(medSpecialtyService.findAll()).willReturn(specialties);

		MvcResult result = mockMvc
			.perform(get("/medSpecialty/all"))
			.andExpect(status().isOk()).andReturn();

		String expectedAsString = objectMapper.writeValueAsString(specialties);
		String resultAsString = result.getResponse().getContentAsString();
		assertEquals(StringUtils.trimAllWhitespace(expectedAsString), StringUtils.trimAllWhitespace(resultAsString));
	}

}
