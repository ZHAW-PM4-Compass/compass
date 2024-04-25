package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import ch.zhaw.pm4.compass.backend.service.TimestampService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DaySheetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DaySheetService daySheetService;

    @MockBean
    private DaySheetRepository daySheetRepository;
    @MockBean
    private TimestampService timestampService;
    @MockBean
    private TimestampRepository timestampRepository;
    static String token ="";

    private String user_id = "löasdjflkajsdf983475908347";
    private LocalDate dateNow = LocalDate.now();
    private String reportText = "Testdate";
    private DaySheetDto getDaySheetDto(){
        return new DaySheetDto(1l,reportText,dateNow,false);
    }
    private DaySheetDto getUpdateDaySheet(){
        return new DaySheetDto(1l,reportText+"1",dateNow.plusDays(1),false);
    }
    private DaySheet getDaySheet(){
        return new DaySheet(1l,reportText,dateNow,false,new ArrayList<Timestamp>());
    }




    @BeforeAll
    public void getToken()
    {
        try {
            token = TestUtils.initMockWithToken(mockMvc);
        }
        catch(Exception ex)
        {
            String errorMessage = ex.getMessage();
            new AssertionError(errorMessage);
        }
    }
    @Test
    void testCreateDay() throws Exception {
        // Arrange



        //CreateDaySheetDto day = getCreateDaySheet();
        DaySheetDto getDay = getDaySheetDto();

        when(daySheetService.createDay(any(DaySheetDto.class),any(String.class))).thenReturn(getDay);


        // Act and Assert//
        mockMvc.perform(post("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{\"day_report\": \""+reportText+"\", \"date\": \"" + dateNow.toString() +"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.dayReport").value(reportText))
                .andExpect(jsonPath("$.date").value(dateNow.toString()));


        verify(daySheetService, times(1)).createDay(any(DaySheetDto.class),any(String.class));
    }
    @Test
    void testCreateDaySheetWithEmptyBody() throws Exception {
        // Arrange



        //CreateDaySheetDto day = getCreateDaySheet();
        DaySheetDto getDay = getDaySheetDto();

        when(daySheetService.createDay(any(DaySheetDto.class),any(String.class))).thenReturn(getDay);


        // Act and Assert//.header("Authorization", "Bearer " + token))
        mockMvc.perform(post("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{}"))
                .andExpect(status().isForbidden());


        verify(daySheetService, times(0)).createDay(any(DaySheetDto.class),any(String.class));
    }
    @Test
    public void testDayAlreadyExists() throws Exception {
        // Arrange
        // Arrange



        DaySheetDto dayDto = getDaySheetDto();

        DaySheet daySheet = getDaySheet();
        when(daySheetRepository.findByDateAndUserId(any(LocalDate.class),any(String.class))).thenReturn(Optional.of(daySheet));
        when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);

        // Act and Assert//.header("Authorization", "Bearer " + token))
        mockMvc.perform(post("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{\"dayReport\": \""+reportText+"\", \"date\": \"" + dateNow.toString() +"\"}"))

                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());



        verify(daySheetService, times(1)).createDay(any(DaySheetDto.class),any(String.class));
    }
    @Test
    void testUpdateDay() throws Exception {
        // Arrange




        DaySheetDto getDay = getDaySheetDto();
        getDay.setDayReport(reportText+"1");
        getDay.setDate(dateNow.plusDays(1));
        DaySheetDto updateDay = getUpdateDaySheet();
        when(daySheetService.updateDay(any(DaySheetDto.class),any(String.class))).thenReturn(updateDay);


        // Act and Assert//.header("Authorization", "Bearer " + token))
        mockMvc.perform(put("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{\"id\": 1,\"day_report\": \""+reportText+"1"+"\", \"date\": \"" + dateNow.toString() +"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.dayReport").value(updateDay.getDayReport()))
                .andExpect(jsonPath("$.date").value(updateDay.getDate().toString()));


        verify(daySheetService, times(1)).updateDay(any(DaySheetDto.class),any(String.class));
    }



    @Test
    void testGetDayByDate() throws Exception {
        // Arrange
        DaySheetDto getDay = getDaySheetDto();
        when(daySheetService.getDaySheetByDate(any(LocalDate.class),any(String.class))).thenReturn(getDay);

        // Act and Assert//.header("Authorization", "Bearer " + token))
        mockMvc.perform(get("/daysheet/getByDate/" + getDay.getDate().toString()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.dayReport").value(getDay.getDayReport()))
                .andExpect(jsonPath("$.date").value(getDay.getDate().toString()));

        verify(daySheetService, times(1)).getDaySheetByDate(any(LocalDate.class),any(String.class));
    }

    @Test
    void testGetDayById() throws Exception {
        // Arrange
        DaySheetDto getDay = getDaySheetDto();
        when(daySheetService.getDaySheetById(any(Long.class),any(String.class))).thenReturn(getDay);

        // Act and Assert//.header("Authorization", "Bearer " + token))
        mockMvc.perform(get("/daysheet/getById/" + getDay.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.dayReport").value(getDay.getDayReport()))
                .andExpect(jsonPath("$.date").value(getDay.getDate().toString()));

        verify(daySheetService, times(1)).getDaySheetById(any(Long.class),any(String.class));
    }
}
