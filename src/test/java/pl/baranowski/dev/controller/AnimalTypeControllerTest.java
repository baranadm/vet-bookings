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
import pl.baranowski.dev.dto.AnimalTypeDTO;
import pl.baranowski.dev.dto.ErrorDTO;
import pl.baranowski.dev.dto.MultiFieldsErrorDTO;
import pl.baranowski.dev.error.FieldValidationError;
import pl.baranowski.dev.exception.EmptyFieldException;
import pl.baranowski.dev.exception.InvalidParamException;
import pl.baranowski.dev.exception.NotFoundException;
import pl.baranowski.dev.exception.animalType.AnimalTypeAlreadyExistsException;
import pl.baranowski.dev.service.AnimalTypeService;

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
class AnimalTypeControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AnimalTypeService animalTypeService;

    private List<AnimalTypeDTO> animalTypesDTOList;

    @BeforeEach
    void setUp() {
        animalTypesDTOList = new ArrayList<>();
        animalTypesDTOList.add(new AnimalTypeDTO(1L, "Kot"));
        animalTypesDTOList.add(new AnimalTypeDTO(2L, "Wiewiórka"));
        animalTypesDTOList.add(new AnimalTypeDTO(3L, "Pies"));
    }


    @Test
    void testFindAll_respondsToRequest() throws Exception {
        mockMvc.perform(get("/animalTypes/")).andExpect(status().isOk());
    }

    @Test
    void testFindAll_returnsEntries() throws Exception {
        given(animalTypeService.findAll()).willReturn(animalTypesDTOList);

        MvcResult result = mockMvc.perform(get("/animalTypes/")).andExpect(status().isOk()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        List<AnimalTypeDTO> resultList = objectMapper.readValue(resultAsString,
                                                                new TypeReference<>() {
                                                                });
        assertEquals(animalTypesDTOList, resultList);
    }


    @Test
    void testFindById_whenValidId_respondsToRequest() throws Exception {
        given(animalTypeService.findById(1L)).willReturn(new AnimalTypeDTO());
        mockMvc.perform(get("/animalTypes/{id}", 1L)).andExpect(status().isOk());
    }

    @Test
    void testFindById_whenValidId_returnsCorrectEntry() throws Exception {
        AnimalTypeDTO expected = new AnimalTypeDTO(1L, "Wiewiórka");
        given(animalTypeService.findById(1L)).willReturn(expected);

        MvcResult result = mockMvc.perform(get("/animalTypes/{id}", 1L)).andExpect(status().isOk()).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        AnimalTypeDTO resultDTO = objectMapper.readValue(resultAsString, AnimalTypeDTO.class);
        assertEquals(expected, resultDTO);
    }

    @Test
    void testFindById_whenInvalidId_returnsError_andStatus400() throws Exception {
        String invalidParam = "aaa";
        InvalidParamException exception = new InvalidParamException("id", invalidParam);
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(get("/animalTypes/{id}", invalidParam))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void testFindById_whenValidIdAndNoEntry_returns404AndHandlesException() throws Exception {
        NotFoundException expectedException = new NotFoundException("Animal type has not been found");
        given(animalTypeService.findById(1L)).willThrow(expectedException);

        MvcResult result = mockMvc.perform(get("/animalTypes/{id}", 1L))
                .andExpect(status().isNotFound())
                .andReturn();

        ErrorDTO expectedError = new ErrorDTO(expectedException);

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void testFindByName_respondsToRequest() throws Exception {
        mockMvc.perform(get("/animalTypes/find").param("name", "Wiewiórka")).andExpect(status().isOk());
    }

    @Test
    void testFindByName_whenNameNotEmpty_correctBusinessCalls() throws Exception {
        mockMvc.perform(get("/animalTypes/find").param("name", "Wiewiórka")).andExpect(status().isOk());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(animalTypeService, times(1)).findByName(captor.capture());
        assertEquals("Wiewiórka", captor.getValue());
    }

    @Test
    void testFindByName_whenNameIsNotEmpty__callsBusinessCorreclty_andReturnsEntries() throws Exception {
        AnimalTypeDTO expectedDTO = new AnimalTypeDTO(1L, "Wiewiórka");
        List<AnimalTypeDTO> expectedDTOlist = Collections.singletonList(expectedDTO);
        given(animalTypeService.findByName(expectedDTO.getName())).willReturn(expectedDTOlist);

        MvcResult result = mockMvc.perform(get("/animalTypes/find").param("name", expectedDTO.getName()))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(animalTypeService, times(1)).findByName(captor.capture());
        assertEquals(expectedDTO.getName(), captor.getValue());

        String resultAsString = result.getResponse().getContentAsString();
        List<AnimalTypeDTO> resultDTO = objectMapper.readValue(resultAsString,
                                                               new TypeReference<>() {
                                                               });
        assertEquals(expectedDTOlist, resultDTO);
    }

    @Test
    void testFindByName_whenNameIsEmpty_returns400andError() throws Exception {
        EmptyFieldException exception = new EmptyFieldException("name");
        ErrorDTO expectedError = new ErrorDTO(exception);

        MvcResult result = mockMvc.perform(get("/animalTypes/find").param("name", ""))
                .andExpect(status().isBadRequest())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }

    @Test
    void testAddNew_respondsToRequest() throws Exception {
        AnimalTypeDTO dto = new AnimalTypeDTO("Wiewiórka");
        mockMvc.perform(
                        post("/animalTypes/new").contentType("application/json").content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testAddNew_whenValidInput_callsBusinessCorrectly_thenReturnsValidAnimalType() throws Exception {
        AnimalTypeDTO newDTO = new AnimalTypeDTO("Wiewiórka");
        AnimalTypeDTO expectedDTO = new AnimalTypeDTO(1L, "Wiewiórka");

        // mocking service return value
        given(animalTypeService.addNew(newDTO)).willReturn(expectedDTO);

        MvcResult result = mockMvc.perform(
                        post("/animalTypes/new")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        ArgumentCaptor<AnimalTypeDTO> animalTypeCaptor = ArgumentCaptor.forClass(AnimalTypeDTO.class);
        verify(animalTypeService, times(1)).addNew(animalTypeCaptor.capture());
        assertEquals(newDTO.getName(), animalTypeCaptor.getValue().getName());

        String resultAsString = result.getResponse().getContentAsString();
        AnimalTypeDTO resultDTO = objectMapper.readValue(resultAsString, AnimalTypeDTO.class);
        assertEquals(expectedDTO, resultDTO);

    }

    @Test
    void testAddNew_whenNameIsEmpty_thenReturns400AndErrorDTO() throws Exception {
        AnimalTypeDTO emptyNameDto = new AnimalTypeDTO("");
        MultiFieldsErrorDTO expectedError = new MultiFieldsErrorDTO(new FieldValidationError("name",
                                                                                             "Field can not be empty."));

        MvcResult result = mockMvc
                .perform(post("/animalTypes/new")
                                 .contentType("application/json")
                                 .content(objectMapper.writeValueAsString(emptyNameDto)))
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
    void testAddNew_whenNameIsDuplicated_thenReturns400AndErrorDTO() throws Exception {
        AnimalTypeDTO requestDto = new AnimalTypeDTO("Wiewiórka");
        AnimalTypeAlreadyExistsException exception = new AnimalTypeAlreadyExistsException(requestDto.getName());
        ErrorDTO expectedError = new ErrorDTO(exception);

        // mocking service method
        given(animalTypeService.addNew(requestDto)).willThrow(exception);

        MvcResult result = mockMvc.perform(post("/animalTypes/new").contentType("application/json")
                                                   .content(objectMapper.writeValueAsString(requestDto))).andReturn();

        String resultAsString = result.getResponse().getContentAsString();
        ErrorDTO resultError = objectMapper.readValue(resultAsString, ErrorDTO.class);
        assertEquals(expectedError, resultError);
    }
}
