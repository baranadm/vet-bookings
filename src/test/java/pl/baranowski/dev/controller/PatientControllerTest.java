package pl.baranowski.dev.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.baranowski.dev.dto.*;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.patient.PatientAlreadyExistsException;
import pl.baranowski.dev.model.RestPageImpl;
import pl.baranowski.dev.service.PatientService;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {
    @MockBean
    PatientService patientService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    private PatientDTO patientDTO;
    private NewPatientDTO newPatientDTO;

    @BeforeEach
    void setUp() {
        patientDTO = new PatientDTO(1L, "Krakers", new AnimalTypeDTO(1L, "Cat"), 5, "Papa Smurf",
                                    "papasmurf@gargamel.com");

        newPatientDTO = new NewPatientDTO(patientDTO.getName(),
                                          patientDTO.getAge().toString(),
                                          patientDTO.getAnimalType().getName(),
                                          patientDTO.getOwnerName(),
                                          patientDTO.getOwnerEmail());
    }

    @Test
    void getById_whenEntityExists_callsCorrectlyAndReturnsDTO() throws Exception {
        given(patientService.getDto(patientDTO.getId())).willReturn(patientDTO);

        MvcResult result = mockMvc.perform(get("/patient/{id}", patientDTO.getId()))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(patientService, times(1)).getDto(idCaptor.capture());
        assertEquals(patientDTO.getId(), idCaptor.getValue()); // verifies business call


        String resultAsString = result.getResponse().getContentAsString();
        PatientDTO resultDTO = objectMapper.readValue(resultAsString, PatientDTO.class);
        assertEquals(patientDTO, resultDTO);
    }

    @Test
    void getById_whenEntityDoesNotExists_throwsNotFoundException() throws Exception {
        NotFoundException expectedException = new NotFoundException("test");
        ErrorDTO expectedError = new ErrorDTO(expectedException);
        given(patientService.getDto(patientDTO.getId())).willThrow(expectedException);

        MvcResult result = mockMvc.perform(get("/patient/{id}", patientDTO.getId()))
                .andExpect(status().isNotFound())
                .andReturn();


        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void getById_whenIdContainsCharacter_throwsErrors_andStatus400() throws Exception {
        MvcResult result = mockMvc.perform(get("/patient/{id}", "a"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assertEquals(1, resultError.getFieldErrors().size());
    }

    @Test
    void getById_whenIdNegative_throwsErrors_andStatus400() throws Exception {
        MvcResult result = mockMvc.perform(get("/patient/{id}", "-1"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assertEquals(1, resultError.getFieldErrors().size());
    }

    @Test
    void findAll_whenNoParams_throwsError_andStatus400() throws Exception {
        MvcResult result = mockMvc.perform(get("/patient/"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, resultError.getHttpStatus());
        assertFalse(resultError.getMessage().isEmpty());
    }

    @Test
    void findAll_whenValidParams_makesCorrectCallsAndReturns200AndDTOsPage() throws Exception {
        Pageable expectedPageable = PageRequest.of(2, 2);
        Page<PatientDTO> expectedPage = new PageImpl<>(Collections.singletonList(patientDTO), expectedPageable, 1);
        given(patientService.findAll(expectedPageable)).willReturn(expectedPage);

        MvcResult result = mockMvc.perform(get("/patient/")
                                                   .contentType("application/json;charset=UTF-8)")
                                                   .param("page", "2")
                                                   .param("size", "2"))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(patientService, times(1)).findAll(pageableCaptor.capture());
        assertEquals(expectedPageable, pageableCaptor.getValue()); // verifies business calls

        String resultAsString = result.getResponse().getContentAsString();
        Page<PatientDTO> resultPage = objectMapper.readValue(resultAsString,
                                                             new TypeReference<RestPageImpl<PatientDTO>>() {
                                                             });
        assertEquals(expectedPage, resultPage);
    }

    @Test
    void findAll_whenInvalidParams_handlesException() throws Exception {
        MvcResult result = mockMvc.perform(get("/patient/")
                                                   .contentType("application/json;charset=UTF-8)")
                                                   .param("page", "")
                                                   .param("size", ""))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assertEquals(4, resultError.getFieldErrors().size());
    }

    @Test
    void addNew_whenInvalidRequestBody_returns404andErrorDTO() throws Exception {
        NewPatientDTO incorrectNewPatientDTO = new NewPatientDTO("", "-1", "", "", "ee");

        mockMvc.perform(post("/patient/").content(objectMapper.writeValueAsString(incorrectNewPatientDTO))
                                .contentType("application/json;charset=UTF-8")).andExpect(status().isBadRequest())
                // checks, if there are 5 errors thrown
                .andExpect(jsonPath("$.fieldErrors", hasSize(5)));

        //for "age": "a"
        incorrectNewPatientDTO.setAge("a");
        mockMvc.perform(post("/patient/").content(objectMapper.writeValueAsString(incorrectNewPatientDTO))
                                .contentType("application/json;charset=UTF-8")).andExpect(status().isBadRequest())
                // checks, if there are 6 errors thrown
                .andExpect(jsonPath("$.fieldErrors", hasSize(6)));
    }

    @Test
    void addNew_whenValidRequestBody_callsBusinessCorrectlyAndReturns201andNewDTO() throws Exception {
        // mocking patientService return value...
        given(patientService.addNew(newPatientDTO)).willReturn(patientDTO);

        // responds to request, returns 201 on success
        MvcResult result = mockMvc.perform(post("/patient/").content(objectMapper.writeValueAsString(newPatientDTO))
                                                   .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andReturn();

        ArgumentCaptor<NewPatientDTO> dtoCaptor = ArgumentCaptor.forClass(NewPatientDTO.class);
        verify(patientService, times(1)).addNew(dtoCaptor.capture());
        assertEquals(newPatientDTO, dtoCaptor.getValue()); // verifies business call

        String resultAsString = result.getResponse().getContentAsString();
        PatientDTO resultDTO = objectMapper.readValue(resultAsString, PatientDTO.class);
        assertEquals(patientDTO, resultDTO);
    }

    @Test
    void addNew_whenAnimalTypeNotExists_handlesNotFoundException() throws Exception {
        NotFoundException exception = new NotFoundException("Animal type not exists.");
        given(patientService.addNew(newPatientDTO)).willThrow(exception);

        MvcResult result = mockMvc.perform(post("/patient/")
                                                   .content(objectMapper.writeValueAsString(newPatientDTO))
                                                   .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound()).andReturn();

        ErrorDTO expectedError = new ErrorDTO(exception);

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addNew_handlesPatientAlreadyExistsException() throws Exception {
        PatientAlreadyExistsException exception = new PatientAlreadyExistsException(newPatientDTO);
        ErrorDTO expectedError = new ErrorDTO(exception);
        given(patientService.addNew(newPatientDTO)).willThrow(exception);

        MvcResult result = mockMvc.perform(post("/patient/").content(objectMapper.writeValueAsString(newPatientDTO))
                                                   .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andReturn();


        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }
}
