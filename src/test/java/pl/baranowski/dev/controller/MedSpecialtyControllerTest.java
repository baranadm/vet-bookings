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
import pl.baranowski.dev.error.FieldValidationError;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.medSpecialty.MedSpecialtyAlreadyExistsException;
import pl.baranowski.dev.service.MedSpecialtyService;

import java.util.ArrayList;
import java.util.Collections;
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

        String resultAsString = result.getResponse().getContentAsString();
        List<MedSpecialtyDTO> resultList = objectMapper.readValue(resultAsString,
                                                                  new TypeReference<>() {
                                                                  });
        assertEquals(specialtiesDTO, resultList);
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
    void getById_whenValidIdAndNoEntry_returns404AndError() throws Exception {
        NotFoundException exception = new NotFoundException("Medical Specialty has not been found");
        ErrorDTO expectedError = new ErrorDTO(exception);

        given(medSpecialtyService.getById(123L)).willThrow(exception);

        MvcResult result = mockMvc.perform(get("/medSpecialty/{id}", "123"))
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

        MvcResult result = mockMvc.perform(get("/medSpecialty/{id}", 1L)).andExpect(status().isOk()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MedSpecialtyDTO resultDTO = objectMapper.readValue(resultAsString, MedSpecialtyDTO.class);
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void getById_whenInvalidId_returns400AndError() throws Exception {
        String invalidParam = "ł";
        InvalidParamException exception = new InvalidParamException("id", invalidParam);
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(get("/medSpecialty/{id}", invalidParam))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
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
        MedSpecialtyDTO dto = new MedSpecialtyDTO(1L, "Kóniolog");
        List<MedSpecialtyDTO> expectedDTOlist = Collections.singletonList(dto);
        given(medSpecialtyService.findByName(dto.getName())).willReturn(expectedDTOlist);

        MvcResult result = mockMvc.perform(get("/medSpecialty/find").param("specialty", dto.getName()))
                .andExpect(status().isOk())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        List<MedSpecialtyDTO> resultDTO = objectMapper.readValue(resultAsString,
                                                                 new TypeReference<>() {
                                                                 });
        assertEquals(expectedDTOlist, resultDTO);
    }

    @Test
    void findByName_whenNameIsEmpty_returns400andError() throws Exception {
        ErrorDTO expectedError = new ErrorDTO(new EmptyFieldException("specialty"));
        MvcResult result = mockMvc.perform(get("/medSpecialty/find").param("specialty", ""))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addNew_respondsToRequest() throws Exception {
        MedSpecialtyDTO body = new MedSpecialtyDTO("ĘŁÓ log");
        mockMvc.perform(
                        post("/medSpecialty/new")
                                .contentType("application/json;charset=UTF-8")
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void addNew_whenValidInput_verifyBusinessCalls() throws Exception {
        MedSpecialtyDTO dto = new MedSpecialtyDTO("ĘŁÓ log");
        mockMvc.perform(
                        post("/medSpecialty/new")
                                .contentType("application/json;charset=UTF-8")
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<MedSpecialtyDTO> medSpecialtyCaptor = ArgumentCaptor.forClass(MedSpecialtyDTO.class);
        verify(medSpecialtyService, times(1)).addNew(medSpecialtyCaptor.capture());

        assertEquals(medSpecialtyCaptor.getValue(), dto);
    }

    // fails: returns empty body
    @Test
    void addNew_whenValidInput_returns200AndNewEntry() throws Exception {
        MedSpecialtyDTO newDTO = new MedSpecialtyDTO("ĘŁÓ log");
        MedSpecialtyDTO expectedDTO = new MedSpecialtyDTO(1L, "ĘŁÓ log");

        // mocking service return value
        given(medSpecialtyService.addNew(newDTO)).willReturn(expectedDTO);

        MvcResult result = mockMvc.perform(
                        post("/medSpecialty/new")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isCreated()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MedSpecialtyDTO resultDTO = objectMapper.readValue(resultAsString, MedSpecialtyDTO.class);
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void addNew_whenEmptyName_returns400AndError() throws Exception {
        MedSpecialtyDTO dto = new MedSpecialtyDTO("");
        MultiFieldsErrorDTO expectedError = new MultiFieldsErrorDTO(new FieldValidationError("name",
                                                                                             "specialty must not be null or empty"));

        MvcResult result = mockMvc.perform(
                        post("/medSpecialty/new")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assert (errorFieldsEquals(expectedError.getFieldErrors(), resultError.getFieldErrors()));
    }

    private boolean errorFieldsEquals(List<FieldValidationError> expected, List<FieldValidationError> actual) {
        for (int i = 0; i < expected.size(); i++) {
            FieldValidationError expectedField = expected.get(i);
            FieldValidationError actualField = actual.get(i);
            if (!expectedField.getField().equals(actualField.getField())) {
                return false;
            }
        }
        return true;
    }

    @Test
    void addNew_whenDuplicatedName_returns400AndError() throws Exception {
        MedSpecialtyDTO newDTO = new MedSpecialtyDTO("ĘŁÓ ziom");
        MedSpecialtyAlreadyExistsException exception = new MedSpecialtyAlreadyExistsException(newDTO.getName());
        ErrorDTO expectedError = new ErrorDTO(exception);

        given(medSpecialtyService.addNew(newDTO))
                .willThrow(exception);

        MvcResult result = mockMvc.perform(
                        post("/medSpecialty/new")
                                .contentType("application/json;charset=UTF-8")
                                .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isForbidden()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

}