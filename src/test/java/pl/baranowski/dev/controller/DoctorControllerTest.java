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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.baranowski.dev.builder.DoctorDTOBuilder;
import pl.baranowski.dev.dto.DoctorDTO;
import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.MultiFieldsErrorDTO;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.doctor.DoctorAlreadyExistsException;
import pl.baranowski.dev.exception.doctor.DoctorDoubledSpecialtyException;
import pl.baranowski.dev.exception.doctor.DoctorNotActiveException;
import pl.baranowski.dev.model.RestPageImpl;
import pl.baranowski.dev.service.DoctorService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//API dla lekarzy
//root path: /doctors
//@POST - should add doctor
//example body: {"name": "xx", "surname": "xx", "type": "xxx", "animalType": "yyy", "salary": 000, "nip": "xxx"}
//response: 201 Created
//response: 400 BAD request. Error handling: duplicated nip, all fields must be not empty, salary cannot be negative.
//
//@GET /{id}
//response: 200 ok. body: {"name": "xx", "surname": "xx", "type": "xxx", "animalType": "yyy", "salary": 000, "nip": "xxx"}
//response: 404 not found - if given id not exists in db.
//
//@GET 
//request parameters: page/size - optional, with default values. Default spring boot pagination expected.
//response: 200 ok: body: page with doctors body content. (nie chce mi sie pisac tutaj:D)
//
//@PUT /fire/{id}
//no body
//response: 200 OK. changed status of given doctor, this doctor will not be able to handle any visits.
//response: 404 NOT FOUND - if given id not exists in db.
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DoctorController.class)
public class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    DoctorService doctorService;
    private DoctorDTO mostowiak;
    private List<DoctorDTO> doctorsList;

    @BeforeEach
    void setUp() {
        mostowiak = new DoctorDTOBuilder().name("Marek")
                                          .surname("Mostowiak")
                                          .id(1L)
                                          .hourlyRate("150")
                                          .nip("1181328620")
                                          .build();

        doctorsList = new ArrayList<>();
        doctorsList.add(new DoctorDTOBuilder().name("Robert")
                                              .surname("Kubica")
                                              .hourlyRate("100000")
                                              .nip("1213141516")
                                              .build());

        doctorsList.add(new DoctorDTOBuilder().name("Mirosław")
                                              .surname("Rosomak")
                                              .hourlyRate("100.0")
                                              .nip("0987654321")
                                              .build());

        doctorsList.add(new DoctorDTOBuilder().name("Mamadou")
                                              .surname("Urghabananandi")
                                              .hourlyRate("40")
                                              .nip("5566557755")
                                              .build());

        doctorsList.add(new DoctorDTOBuilder().name("C")
                                              .surname("J")
                                              .hourlyRate("123.45")
                                              .nip("1122334455")
                                              .build());
    }

    @Test
        // request: @GET /{id}
    void getById_respondsToRequest() throws Exception {
        mockMvc.perform(get("/doctors/{id}", 1)).andExpect(status().isOk());
    }

    @Test
    void getById_whenValidId_returnsCorrectValue_andStatus200() throws Exception {
        //given
        DoctorDTO expected = this.mostowiak;
        given(doctorService.getDTO(expected.getId())).willReturn(expected);

        //when
        MvcResult result = mockMvc.perform(get("/doctors/{id}", expected.getId()))
                                  .andExpect(status().isOk())
                                  .andReturn();

        //then
        String resultAsString = result.getResponse().getContentAsString();
        DoctorDTO actual = objectMapper.readValue(resultAsString, DoctorDTO.class);
        assertEquals(expected, actual);
    }

    @Test
    void getById_whenInvalidId_returnsError_andStatus400() throws Exception {
        String invalidId = "eee";
        InvalidParamException exception = new InvalidParamException("id", invalidId);
        ErrorDTO expectedError = new ErrorDTO(exception);
        MvcResult result = mockMvc.perform(get("/doctors/{id}", invalidId))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void getById_whenValidIdAndNoEntry_returns404AndError() throws Exception {
        NotFoundException exception = new NotFoundException("Doctor with id=" + 1L + " has not been found.");
        ErrorDTO expectedError = new ErrorDTO(exception);
        given(doctorService.getDTO(1L)).willThrow(exception);

        MvcResult result = mockMvc.perform(get("/doctors/{id}", 1)).andExpect(status().isNotFound()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }


    //@GET
    //request parameters: page/size - optional, with default values.
    @Test
    void findAll_respondsToRequest() throws Exception {
        Pageable request = PageRequest.of(0, 3);
        Page<DoctorDTO> resultPage = Page.empty(request);

        given(doctorService.findAll(request)).willReturn(resultPage);

        mockMvc.perform(get("/doctors/").param("page", "0").param("size", "3")).andExpect(status().isOk());
    }

    @Test
    void findAll_withPaginationAttr_callsForEntriesWithValidPagination() throws Exception {
        Pageable expectedRequest = PageRequest.of(0, 3);
        Page<DoctorDTO> resultPage = Page.empty(expectedRequest);
        given(doctorService.findAll(expectedRequest)).willReturn(resultPage);

        mockMvc.perform(get("/doctors/").param("page", "0").param("size", "3")).andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(doctorService).findAll(pageableCaptor.capture());
        assertEquals(expectedRequest, pageableCaptor.getValue());
    }

    @Test
    void findAll_whenNoPaginationAttr_callsForEntriesWithDefaultPagination() throws Exception {
        Pageable expectedDefaultRequest = DoctorController.DEFAULT_PAGEABLE;
        Page<DoctorDTO> resultPage = Page.empty(expectedDefaultRequest);
        given(doctorService.findAll(expectedDefaultRequest)).willReturn(resultPage);

        mockMvc.perform(get("/doctors/").param("page", "" + expectedDefaultRequest.getPageNumber())
                                        .param("size", "" + expectedDefaultRequest.getPageSize()))
               .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(doctorService).findAll(pageableCaptor.capture());
        assertEquals(expectedDefaultRequest, pageableCaptor.getValue());
    }

    @Test
    void findAll_invalidPageAndSize_returnsErrors_andStatus400() throws Exception {
        MvcResult result = mockMvc.perform(get("/doctors/").param("page", "-1").param("size", ""))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        MultiFieldsErrorDTO resultError = objectMapper.readValue(resultAsString, MultiFieldsErrorDTO.class);
        assertEquals(3, resultError.getFieldErrors().size());
    }

    @Test
    void findAll_whenValidInput_returnsDoctorsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 3);
        Page<DoctorDTO> expectedPage = new PageImpl<>(doctorsList, pageable, doctorsList.size());

        given(doctorService.findAll(pageable)).willReturn(expectedPage);

        MvcResult result = mockMvc.perform(get("/doctors/").param("page", "" + pageable.getPageNumber())
                                                           .param("size", "" + pageable.getPageSize()))
                                  .andExpect(status().isOk())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        TypeReference<RestPageImpl<DoctorDTO>> pageType = new TypeReference<>() {
        };
        Page<DoctorDTO> resultPage = objectMapper.readValue(resultAsString, pageType);
        assertEquals(expectedPage, resultPage);
    }

    //@POST - should add doctor
    //example body: {"name": "xx", "surname": "xx", "type": "xxx", "animalType": "yyy", "salary": 000, "nip": "xxx"}
    //response: 201 Created
    //response: 400 BAD request. Error handling: duplicated nip, all fields must be not empty, salary cannot be negative.
    @Test
    void addNew_respondsToRequest() throws Exception {
        DoctorDTO expected = mostowiak;
        mockMvc.perform(post("/doctors/").contentType("application/json")
                                         .characterEncoding("UTF-8")
                                         .content(objectMapper.writeValueAsString(expected)))
               .andExpect(status().isCreated());
    }

    @Test
    void addNew_whenValidRequestBody_returns201AndEntry() throws Exception {
        DoctorDTO requestDTO = new DoctorDTOBuilder().name(mostowiak.getName())
                                                     .surname(mostowiak.getSurname())
                                                     .id(mostowiak.getId())
                                                     .hourlyRate(mostowiak.getHourlyRate())
                                                     .nip(mostowiak.getNip())
                                                     .build();
        DoctorDTO expectedDTO = mostowiak;
        given(doctorService.addNew(requestDTO)).willReturn(expectedDTO);

        MvcResult result = mockMvc.perform(post("/doctors/").contentType("application/json")
                                                            .characterEncoding("UTF-8")
                                                            .content(objectMapper.writeValueAsString(requestDTO)))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        DoctorDTO resultDTO = objectMapper.readValue(resultAsString, DoctorDTO.class);
        assertEquals(expectedDTO, resultDTO);

    }

    @Test
    void addNew_whenValidRequestAndNIPExists_returnsError_andStatus400() throws Exception {
        DoctorDTO requestDTO = mostowiak;
        DoctorAlreadyExistsException exception = new DoctorAlreadyExistsException(requestDTO.getNip());
        ErrorDTO expectedError = new ErrorDTO(exception);
        given(doctorService.addNew(requestDTO)).willThrow(exception);

        MvcResult result = mockMvc.perform(post("/doctors/").contentType("application/json")
                                                            .characterEncoding("UTF-8")
                                                            .content(objectMapper.writeValueAsString(requestDTO)))
                                  .andExpect(status().isForbidden())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addNew_whenAllFieldsNotValid_returnsErrorForEveryField_andStatus400() throws Exception {
        DoctorDTO requestDTO = new DoctorDTOBuilder().name("").surname("").hourlyRate("a1").nip("1111111112")
                                                     .build();

        mockMvc.perform(post("/doctors/").contentType("application/json")
                                         .characterEncoding("UTF-8")
                                         .content(objectMapper.writeValueAsString(requestDTO)))
               .andExpect(status().isBadRequest())

               // checks, if there are 4 errors thrown
               .andExpect(jsonPath("$.fieldErrors", hasSize(4)));
    }

    //@PUT /fire/{id}
    //no body
    //response: 200 OK. changed status of given doctor, this doctor will not be able to handle any visits.
    //response: 404 NOT FOUND - if given id not exists in db.

    @Test
    void fire_respondsToRequest() throws Exception {
        mockMvc.perform(put("/doctors/fire/{id}", 1L).characterEncoding("UTF-8")).andExpect(status().isOk());
    }

    @Test
    void fire_whenValidId_callsFireWithCorrectId_andReturns200OnSuccess_andReturnsCorrectValue() throws Exception {
        DoctorDTO fired = new DoctorDTOBuilder().id(1L).name("Mark").surname("Second").active(false)
                                                .build();
        given(doctorService.fire(fired.getId())).willReturn(fired);

        MvcResult result = mockMvc.perform(put("/doctors/fire/{id}", fired.getId()).characterEncoding("UTF-8"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(doctorService, times(1)).fire(captor.capture());
        assertEquals(fired.getId(), captor.getValue());

        String resultAsString = result.getResponse().getContentAsString();
        DoctorDTO resultDTO = objectMapper.readValue(resultAsString, DoctorDTO.class);
        assertEquals(fired, resultDTO);
    }

    @Test
    void fire_whenValidId_callsFireWithCorrectId_andWhenNoEntry_Returns404AndError() throws Exception {
        Long id = 1L;
        NotFoundException exception = new NotFoundException("Doctor with id " + id + " not found!");
        ErrorDTO expectedError = new ErrorDTO(exception);
        doThrow(exception).when(doctorService).fire(id);

        MvcResult result = mockMvc.perform(put("/doctors/fire/{id}", 1L).characterEncoding("UTF-8"))
                                  .andExpect(status().isNotFound())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError.getHttpStatus(), resultError.getHttpStatus());
    }

    @Test
    void fire_whenInvalidId_returnsError_andStatus400() throws Exception {
        String invalidId = "prr";
        InvalidParamException exception = new InvalidParamException("id", invalidId);
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(put("/doctors/fire/{id}", invalidId).characterEncoding("UTF-8"))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void fire_handlesException() throws Exception {
        NotFoundException exception = new NotFoundException("Doctor not found.");
        ErrorDTO expectedError = new ErrorDTO(exception);
        doThrow(exception).when(doctorService).fire(mostowiak.getId());

        MvcResult result = mockMvc.perform(put("/doctors/fire/{id}", "1")).andExpect(status().isNotFound()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError.getHttpStatus(), resultError.getHttpStatus());
    }

    @Test
    void addAnimalType_respondsToRequest_AndCorrectBusinessCalls_AndCorrectReturnValue() throws Exception {
        String doctorId = "1";
        String atId = "1";
        given(doctorService.addAnimalType(1L, 1L)).willReturn(mostowiak);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addAnimalType/{id}", doctorId, atId))
                                  .andExpect(status().isOk())
                                  .andReturn();

        ArgumentCaptor<Long> doctorIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> atIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(doctorService, times(1)).addAnimalType(doctorIdCaptor.capture(), atIdCaptor.capture());
        assertEquals(doctorId, doctorIdCaptor.getValue().toString());
        assertEquals(atId, atIdCaptor.getValue().toString());

        String resultAsString = result.getResponse().getContentAsString();
        DoctorDTO updatedDoctorDTO = objectMapper.readValue(resultAsString, DoctorDTO.class);
        assertEquals(mostowiak, updatedDoctorDTO);
    }

    @Test
    void addAnimalType_whenDoctorIdInvalid_handlesBadRequestException() throws Exception {
        String invalidId = "p";
        InvalidParamException exception = new InvalidParamException("id", invalidId);
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addAnimalType/{id}", invalidId, "1"))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addAnimalType_whenAnimalTypeIdInvalid_handlesBadRequestException() throws Exception {
        String invalidId = "p";
        InvalidParamException exception = new InvalidParamException("id", invalidId);
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addAnimalType/{id}", "1", invalidId))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addAnimalType_handlesNotFoundException() throws Exception {
        NotFoundException exception = new NotFoundException("AnimalType not found.");
        ErrorDTO expectedError = new ErrorDTO(exception);
        doThrow(exception).when(doctorService).addAnimalType(1L, 1L);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addAnimalType/{id}", "1", "1"))
                                  .andExpect(status().isNotFound())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addAnimalType_whenAnimalTypeExists_handlesForbiddenException() throws Exception {
        DoctorDoubledSpecialtyException exception = new DoctorDoubledSpecialtyException("Animal Type");
        ErrorDTO expectedError = new ErrorDTO(exception);
        doThrow(exception).when(doctorService).addAnimalType(1L, 1L);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addAnimalType/{id}", "1", "1"))
                                  .andExpect(status().isForbidden())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addAnimalType_whenDoctorNotActive_handlesForbiddenException() throws Exception {
        DoctorNotActiveException exception = new DoctorNotActiveException(1L);
        ErrorDTO expectedError = new ErrorDTO(exception);
        doThrow(exception).when(doctorService).addAnimalType(1L, 1L);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addAnimalType/{id}", "1", "1"))
                                  .andExpect(status().isForbidden())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addMedSpecialty_respondsToRequest_AndCorrectBusinessCalls_AndCorrectReturnValue() throws Exception {
        String doctorId = "1";
        String msId = "1";
        given(doctorService.addMedSpecialty(1L, 1L)).willReturn(mostowiak);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addMedSpecialty/{id}", doctorId, msId))
                                  .andExpect(status().isOk())
                                  .andReturn();

        ArgumentCaptor<Long> doctorIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> msIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(doctorService, times(1)).addMedSpecialty(doctorIdCaptor.capture(), msIdCaptor.capture());
        assertEquals(doctorId, doctorIdCaptor.getValue().toString());
        assertEquals(msId, msIdCaptor.getValue().toString());

        String resultAsString = result.getResponse().getContentAsString();
        DoctorDTO updatedDoctorDTO = objectMapper.readValue(resultAsString, DoctorDTO.class);
        assertEquals(mostowiak, updatedDoctorDTO);
    }

    @Test
    void addMedSpecialty_whenDoctorIdInvalid_handlesBadRequestException() throws Exception {
        String invalidId = "p";
        InvalidParamException exception = new InvalidParamException("id", invalidId);
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addMedSpecialty/{id}", invalidId, "1"))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addMedSpecialty_whenMedSpecialtyIdInvalid_handlesBadRequestException() throws Exception {
        String invalidId = "p";
        InvalidParamException exception = new InvalidParamException("id", invalidId);
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addMedSpecialty/{id}", "1", invalidId))
                                  .andExpect(status().isBadRequest())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addMedSpecialty_handlesEntityNotFoundException() throws Exception {
        NotFoundException exception = new NotFoundException("MedSpecialty not found.");
        ErrorDTO expectedError = new ErrorDTO(exception);
        doThrow(exception).when(doctorService).addMedSpecialty(1L, 1L);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addMedSpecialty/{id}", "1", "1"))
                                  .andExpect(status().isNotFound())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addMedSpecialty_handlesDoubledSpecialtyException() throws Exception {
        DoctorDoubledSpecialtyException exception = new DoctorDoubledSpecialtyException("Medical Specialty");
        ErrorDTO expectedError = new ErrorDTO(exception);
        doThrow(exception).when(doctorService).addMedSpecialty(1L, 1L);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addMedSpecialty/{id}", "1", "1"))
                                  .andExpect(status().isForbidden())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void addMedSpecialty_handlesDoctorNotActiveException() throws Exception {
        DoctorNotActiveException exception = new DoctorNotActiveException(1L);
        ErrorDTO expectedError = new ErrorDTO(exception);
        doThrow(exception).when(doctorService).addMedSpecialty(1L, 1L);

        MvcResult result = mockMvc.perform(put("/doctors/{id}/addMedSpecialty/{id}", "1", "1"))
                                  .andExpect(status().isForbidden())
                                  .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

}