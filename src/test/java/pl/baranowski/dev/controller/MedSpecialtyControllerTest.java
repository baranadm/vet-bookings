package pl.baranowski.dev.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.MedSpecialtyDTO;
import pl.baranowski.dev.dto.MultiFieldsErrorDTO;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.service.MedSpecialtyService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MedSpecialtyControllerTest {

    private final List<MedSpecialtyDTO> specialtiesDTO = new ArrayList<>();
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    MedSpecialtyService medSpecialtyService;

    public MedSpecialtyControllerTest() {
        // some data for testing purposes
        specialtiesDTO.add(new MedSpecialtyDTO("Kardiolog"));
        specialtiesDTO.add(new MedSpecialtyDTO("Neurolog"));
        specialtiesDTO.add(new MedSpecialtyDTO("Brzucholog"));
    }

    @BeforeEach
    void setUp() {
    }

    // verify if controller responds for request
    @Test
    void findAll_respondsToRequest() throws Exception {
        mockMvc
                .perform(get("/medSpecialties/all"))
                .andExpect(status().isOk());
    }

    // verify if controller returns correct entries
    @Test
    void findAll_returnsEntries() throws Exception {
        // mock service return value
        given(medSpecialtyService.findAll()).willReturn(specialtiesDTO);

        MvcResult result = mockMvc
                .perform(get("/medSpecialties/all"))
                .andExpect(status().isOk()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        List<MedSpecialtyDTO> resultList = objectMapper.readValue(resultAsString,
                                                                  new TypeReference<>() {
                                                                  });
        assertEquals(specialtiesDTO, resultList);
    }

    @Test
    void getById_respondsToRequest() throws Exception {
        mockMvc.perform(get("/medSpecialties/{id}", 1L)).andExpect(status().isOk());
    }

    @Test
    void getById_whenValidInput_verifyBusinessCalls() throws Exception {
        String idString = "123";
        mockMvc.perform(get("/medSpecialties/{id}", "123")).andExpect(status().isOk());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(medSpecialtyService, times(1)).getById(captor.capture());
        assertEquals(Long.decode(idString), captor.getValue());
    }

    @Test
    void getById_whenValidIdAndNoEntry_returns404AndError() throws Exception {
        NotFoundException exception = new NotFoundException("Medical Specialty has not been found");
        ErrorDTO expectedError = new ErrorDTO(exception);

        given(medSpecialtyService.getById(123L)).willThrow(exception);

        MvcResult result = mockMvc.perform(get("/medSpecialties/{id}", "123"))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);

    }

    @Test
    void getById_whenValidId_returnsEntry() throws Exception {
        MedSpecialtyDTO expectedDTO = new MedSpecialtyDTO(1L, "ĘÓŁĄĆŃŻŻ");
        given(medSpecialtyService.getById(1L)).willReturn(expectedDTO);

        MvcResult result = mockMvc.perform(get("/medSpecialties/{id}", 1L)).andExpect(status().isOk()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MedSpecialtyDTO resultDTO = objectMapper.readValue(resultAsString, MedSpecialtyDTO.class);
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void getById_whenInvalidId_returns400AndError() throws Exception {
        String invalidParam = "ł";
        InvalidParamException exception = new InvalidParamException("id", invalidParam);
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(get("/medSpecialties/{id}", invalidParam))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void findByName_respondsToRequest() throws Exception {
        MedSpecialtyDTO expected = new MedSpecialtyDTO(1L, "Kóniolog");
        mockMvc.perform(get("/medSpecialties/find").param("specialty", expected.getName())).andExpect(status().isOk());
    }

    @Test
    void findByName_whenValidInput_verifyBusinessCalls() throws Exception {
        String medSpecialtyName = "Kónio log";
        mockMvc.perform(get("/medSpecialties/find").param("specialty", medSpecialtyName)).andExpect(status().isOk());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(medSpecialtyService, times(1)).findByName(captor.capture());
        assertEquals(medSpecialtyName, captor.getValue());
    }

    @Test
    void findByName_whenValidName_returnsCorrectEntries() throws Exception {
        MedSpecialtyDTO expectedDTO = new MedSpecialtyDTO(1L, "Kóniolog");
        given(medSpecialtyService.findByName(expectedDTO.getName())).willReturn(expectedDTO);

        MvcResult result = mockMvc.perform(get("/medSpecialties/find").param("specialty", expectedDTO.getName()))
                .andExpect(status().isOk())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MedSpecialtyDTO resultDTO = objectMapper.readValue(resultAsString, MedSpecialtyDTO.class);
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void findByName_whenNameIsEmpty_returns400andError() throws Exception {
        MvcResult result = mockMvc.perform(get("/medSpecialties/find").param("specialty", ""))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assertEquals(1, resultError.getFieldErrors().size());
    }

    @Test
    void addNew_respondsToRequest() throws Exception {
        mockMvc.perform(
                        post("/medSpecialties/new")
                                .contentType("application/json;charset=UTF-8")
                                .param("specialty", "Wiewiórka"))
                .andExpect(status().isCreated());
    }

    @Test
    void addNew_whenValidInput_verifyBusinessCalls() throws Exception {
        String specialtyName = "Czółkolog";
        mockMvc.perform(
                        post("/medSpecialties/new")
                                .contentType("application/json;charset=UTF-8")
                                .param("specialty", specialtyName))
                .andExpect(status().isCreated());

        ArgumentCaptor<String> specialtyNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(medSpecialtyService, times(1)).addNew(specialtyNameCaptor.capture());

        assertEquals(specialtyNameCaptor.getValue(), specialtyName);
    }

    @Test
    void addNew_whenValidInput_returns200AndNewEntry() throws Exception {
        String specialtyName = "Czółkolog";
        MedSpecialtyDTO expectedDTO = new MedSpecialtyDTO(1L, specialtyName);

        // mocking service return value
        given(medSpecialtyService.addNew(specialtyName)).willReturn(expectedDTO);

        MvcResult result = mockMvc.perform(
                        post("/medSpecialties/new")
                                .contentType("application/json")
                                .param("specialty", specialtyName))
                .andExpect(status().isCreated()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MedSpecialtyDTO resultDTO = objectMapper.readValue(resultAsString, MedSpecialtyDTO.class);
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void addNew_whenEmptyName_returns400AndError() throws Exception {
        MvcResult result = mockMvc.perform(
                        post("/medSpecialties/new")
                                .contentType("application/json")
                                .param("specialty", ""))
                .andExpect(status().isBadRequest()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assertEquals(1, resultError.getFieldErrors().size());
    }

    @Test
    void addNew_whenDuplicatedName_returns400AndError() throws Exception {
        String specialtyName = "Czółkolog";
        MedSpecialtyAlreadyExistsException exception = new MedSpecialtyAlreadyExistsException(specialtyName);
        ErrorDTO expectedError = new ErrorDTO(exception);

        given(medSpecialtyService.addNew(specialtyName))
                .willThrow(exception);

        MvcResult result = mockMvc.perform(
                        post("/medSpecialties/new")
                                .contentType("application/json;charset=UTF-8")
                                .param("specialty", specialtyName))
                .andExpect(status().isForbidden()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

}