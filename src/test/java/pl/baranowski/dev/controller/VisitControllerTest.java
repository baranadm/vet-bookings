package pl.baranowski.dev.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.dto.*;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.model.RestPageImpl;
import pl.baranowski.dev.service.VisitService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = VisitController.class)
class VisitControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    VisitService visitService;
    private AnimalTypeDTO animalType;
    private DoctorDTO doctor;
    private PatientDTO patient;

    @BeforeEach
    void setUp() {
        animalType = new AnimalTypeDTO(3L, "Wielbłąd");
        doctor = new DoctorDTOBuilder().name("Robert")
                .surname("Kupicha")
                .id(1L)
                .hourlyRate("600")
                .nip("1111111111")
                .build();
        patient = new PatientDTO(2L, "Maniek", animalType, 8, "Lucyna Brzoza", "brzozazlasuobok@gmail.com");
    }

    @Test
    void getById_whenValidId_correctCallsAndReturnValue() throws Exception {
        long epoch = System.currentTimeMillis();
        VisitDTO expectedDTO = new VisitDTO(1L, doctor, patient, epoch, false);

        // mocks service return value
        given(visitService.getById(expectedDTO.getId())).willReturn(expectedDTO);

        MvcResult result = mockMvc.perform(get("/visit/{id}", expectedDTO.getId())
                                                   .contentType("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        // verifies service call
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(visitService, times(1)).getById(idCaptor.capture());
        assertEquals(expectedDTO.getId(), idCaptor.getValue());

        String resultAsString = result.getResponse().getContentAsString();
        VisitDTO resultDTO = objectMapper.readValue(resultAsString, VisitDTO.class);
        assertEquals(expectedDTO, resultDTO);
    }

    @Test
    void getById_whenValidIdAndEntityNotFound_returns404AndNotFound() throws Exception {
        long epoch = System.currentTimeMillis();
        VisitDTO notExisting = new VisitDTO(1L, doctor, patient, epoch, false);
        NotFoundException exception = new NotFoundException("Visit with id=" + notExisting.getId() + " has not been found.");
        ErrorDTO expectedError = new ErrorDTO(exception);

        // mocks service return value
        given(visitService.getById(notExisting.getId())).willThrow(exception);

        MvcResult result = mockMvc.perform(get("/visit/{id}", notExisting.getId())
                                                   .contentType("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andReturn();

        // verifies JSON error response
        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void getById_whenInvalidId_returnsError_and400() throws Exception {
        String invalidId = "a";
        MvcResult result = mockMvc.perform(get("/visit/{id}", invalidId)
                                                   .contentType("application/json;charset=UTF-8"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assertEquals(2, resultError.getFieldErrors().size());
    }

    @Test
    void findAll_withPaginationAttr_callsServiceAndReturnsPage() throws Exception {
        // creates mocked result
        List<VisitDTO> visits = Collections.nCopies(3,
                                                    new VisitDTO(doctor,
                                                                 patient,
                                                                 System.currentTimeMillis() + 60 * 60 * 24 * 5,
                                                                 // + 5 days
                                                                 false));
        Pageable pageable = PageRequest.of(0, 3);
        Page<VisitDTO> expectedPage = new PageImpl<>(visits, pageable, visits.size());

        // mocks service
        given(visitService.findAll(pageable)).willReturn(expectedPage);
        MvcResult result = mockMvc.perform(get("/visit/")
                                                   .param("page", "0")
                                                   .param("size", "3"))
                .andExpect(status().isOk())
                .andReturn();

        // verifies business call
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(visitService, times(1)).findAll(pageableCaptor.capture());
        assertEquals(pageable, pageableCaptor.getValue());

        String resultAsString = result.getResponse().getContentAsString();
        Page<VisitDTO> resultPage = objectMapper.readValue(resultAsString, new TypeReference<RestPageImpl<VisitDTO>>() {
        });
        assertEquals(expectedPage, resultPage);
    }

    @Test
    void findAll_whenNoPaginationAttr_callsServiceWithDefaultPagination() throws Exception {
        Pageable defaultPaginable = VisitController.DEFAULT_PAGEABLE;

        mockMvc.perform(get("/visit/")
                                .param("page", "" + defaultPaginable.getPageNumber())
                                .param("size", "" + defaultPaginable.getPageSize()))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(visitService).findAll(pageableCaptor.capture());
        assertEquals(defaultPaginable, pageableCaptor.getValue());
    }

    @Test
    void findAll_paramsInvalid_returnsErrors_and400() throws Exception {
        MvcResult result = mockMvc.perform(get("/visit/")
                                                   .param("page", "-1.1")
                                                   .param("size", ""))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assertEquals(3, resultError.getFieldErrors().size());
    }

    @Test
    void addNew_whenCorrectInputs_respondsWith201_andCallsServiceCorrectly_andReturnsDTOs() throws Exception {
        Long epoch = 1643157711000L; //26 January 2022 00:41:51

        NewVisitDTO newVisitDTO = new NewVisitDTO(doctor.getId().toString(),
                                                  patient.getId().toString(),
                                                  epoch.toString());

        VisitDTO expectedDTO = new VisitDTO(doctor, patient, epoch, false).withId(4L);

        // mocking visitService.addNew()
        given(visitService.addNew(doctor.getId(), patient.getId(), epoch)).willReturn(expectedDTO);

        MvcResult result = mockMvc.perform(post("/visit/")
                                                   .content(objectMapper.writeValueAsString(newVisitDTO))
                                                   .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();

        // verifies business call
        ArgumentCaptor<Long> doctorIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> patientIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> epochCaptor = ArgumentCaptor.forClass(Long.class);

        verify(visitService, times(1)).addNew(doctorIdCaptor.capture(),
                                              patientIdCaptor.capture(),
                                              epochCaptor.capture());
        assertEquals(doctor.getId(), doctorIdCaptor.getValue());
        assertEquals(patient.getId(), patientIdCaptor.getValue());
        assertEquals(epoch, epochCaptor.getValue());

        String resultAsString = result.getResponse().getContentAsString();
        VisitDTO resultDTO = objectMapper.readValue(resultAsString, VisitDTO.class);
        assertEquals(expectedDTO, resultDTO);
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
        NotFoundException exception = new NotFoundException("Doctor with id=" + newVisit.getDoctorId() + " has not been found.");

        given(visitService.addNew(1L, 2L, now)).willThrow(exception);

        MvcResult result = mockMvc.perform(post("/visit/")
                                                   .content(objectMapper.writeValueAsString(newVisit))
                                                   .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();

        ErrorDTO expectedError = new ErrorDTO(exception);

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void findFreeSlots_returnsCorrectResponseBody_andStatus200() throws JsonProcessingException, Exception {
        // 2100-01-25 10:00:00
        Long MONDAY_H10Y2100 = ZonedDateTime.of(LocalDateTime.of(2100, 1, 25, 10, 00, 00), ZoneId.systemDefault())
                .toEpochSecond();
        // 2100-01-25 11:00:00
        Long MONDAY_H11Y2100 = MONDAY_H10Y2100 + 1 * 60 * 60;
        // 2100-01-25 12:00:00
        Long MONDAY_H12Y2100 = MONDAY_H10Y2100 + 2 * 60 * 60;
        // 2100-01-25 13:00:00
        Long MONDAY_H13Y2100 = MONDAY_H10Y2100 + 3 * 60 * 60;
        // 2100-01-25 14:00:00
        Long MONDAY_H14Y2100 = MONDAY_H10Y2100 + 4 * 60 * 60;
        // 2100-01-25 15:00:00
        Long MONDAY_H15Y2100 = MONDAY_H10Y2100 + 5 * 60 * 60;

        AnimalTypeDTO animalTypeDTO = new AnimalTypeDTO(11L, "Koty");
        MedSpecialtyDTO medSpecialtyDTO = new MedSpecialtyDTO(12L, "Urolog");
        DoctorDTO doctor1dto = new DoctorDTOBuilder().name("Olaf")
                .surname("Lubaszenko")
                .hourlyRate("200.00")
                .nip("1111111111")
                .animalTypes(Collections.singleton(animalTypeDTO))
                .medSpecialties(Collections.singleton(medSpecialtyDTO))
                .build();
        DoctorDTO doctor2dto = new DoctorDTOBuilder().name("Nikita")
                .surname("Mazepin")
                .hourlyRate("100.00")
                .nip("1181328620")
                .animalTypes(Collections.singleton(animalTypeDTO))
                .medSpecialties(Collections.singleton(medSpecialtyDTO))
                .build();

        List<AvailableSlotsAtTheDoctorDTO> expectedResult = new ArrayList<>();
        List<Long> doctor1FreeSlots = Arrays.asList(MONDAY_H10Y2100, MONDAY_H12Y2100, MONDAY_H13Y2100);
        expectedResult.add(new AvailableSlotsAtTheDoctorDTO(doctor1dto, doctor1FreeSlots));
        List<Long> doctor2FreeSlots = Arrays.asList(MONDAY_H11Y2100, MONDAY_H14Y2100, MONDAY_H15Y2100);
        expectedResult.add(new AvailableSlotsAtTheDoctorDTO(doctor2dto, doctor2FreeSlots));

        // times: start and end
        String start = MONDAY_H10Y2100.toString();
        String end = MONDAY_H15Y2100.toString();

        given(visitService.findAvailableSlotsAtTheDoctorsWithParams(animalTypeDTO.getName(),
                                                                    medSpecialtyDTO.getName(),
                                                                    start,
                                                                    end)).willReturn(expectedResult);

        MvcResult result = mockMvc.perform(get("/visit/check")
                                                   .param("animalTypeName", animalTypeDTO.getName())
                                                   .param("medSpecialtyName", medSpecialtyDTO.getName())
                                                   .param("epochStart", start)
                                                   .param("epochEnd", end)
                                                   .param("interval", "3600"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        List<AvailableSlotsAtTheDoctorDTO> actualResult = objectMapper.readValue(resultAsString,
                                                                                 new TypeReference<>() {
                                                                                 });
        assertEquals(expectedResult, actualResult);
    }

    private void mockMvcPerformAndExpect(NewVisitDTO requestDTO, ResultMatcher httpStatusMatcher, String field)
            throws Exception {
        mockMvc.perform(post("/visit/")
                                .content(objectMapper.writeValueAsString(requestDTO))
                                .contentType("application/json;charset=UTF-8"))
                .andExpect(httpStatusMatcher)
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.fieldErrors[*].field", Matchers.hasItems(field)));
    }

}