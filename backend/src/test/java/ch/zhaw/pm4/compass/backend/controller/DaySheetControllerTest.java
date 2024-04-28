package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import ch.zhaw.pm4.compass.backend.service.TimestampService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration
public class DaySheetControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private WebApplicationContext controller;

    @MockBean
    private DaySheetService daySheetService;

    @MockBean
    private DaySheetRepository daySheetRepository;
    @MockBean
    private TimestampService timestampService;
    @MockBean
    private TimestampRepository timestampRepository;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    private LocalDate dateNow = LocalDate.now();
    private String reportText = "Testdate";

    private DaySheetDto getDaySheetDto() {
        return new DaySheetDto(1l, reportText, dateNow, false);
    }

    private DaySheetDto getUpdateDaySheet() {
        return new DaySheetDto(1l, reportText + "1", dateNow.plusDays(1), false);
    }

    private DaySheet getDaySheet() {
        return new DaySheet(1l, reportText, dateNow, false, new ArrayList<Timestamp>());
    }


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testCreateDay() throws Exception {
        // Arrange


        //CreateDaySheetDto day = getCreateDaySheet();
        DaySheetDto getDay = getDaySheetDto();

        when(daySheetService.createDay(any(DaySheetDto.class), any(String.class))).thenReturn(getDay);


        // Act and Assert//
        mockMvc.perform(post("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"day_report\": \"" + reportText + "\", \"date\": \"" + dateNow.toString() + "\"}").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.day_report").value(reportText))
                .andExpect(jsonPath("$.date").value(dateNow.toString()));


        verify(daySheetService, times(1)).createDay(any(DaySheetDto.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testFailsForTestingGithubAction() {
        assertTrue(false);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testCreateDaySheetWithEmptyBody() throws Exception {
        // Arrange
        DaySheetDto getDay = getDaySheetDto();

        when(daySheetService.createDay(any(DaySheetDto.class), any(String.class))).thenReturn(getDay);
        mockMvc.perform(post("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());


        verify(daySheetService, times(0)).createDay(any(DaySheetDto.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    public void testDayAlreadyExists() throws Exception {
        // Arrange


        DaySheet daySheet = getDaySheet();
        when(daySheetRepository.findByDateAndUserId(any(LocalDate.class), any(String.class))).thenReturn(Optional.of(daySheet));
        when(daySheetRepository.save(any(DaySheet.class))).thenReturn(daySheet);

        mockMvc.perform(post("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"day_report\": \"" + reportText + "\", \"date\": \"" + dateNow.toString() + "\"}")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());


        verify(daySheetService, times(1)).createDay(any(DaySheetDto.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testUpdateDay() throws Exception {
        // Arrange


        DaySheetDto getDay = getDaySheetDto();
        getDay.setDay_report(reportText + "1");
        getDay.setDate(dateNow.plusDays(1));
        DaySheetDto updateDay = getUpdateDaySheet();
        when(daySheetService.updateDay(any(DaySheetDto.class), any(String.class))).thenReturn(updateDay);


        //Act
        mockMvc.perform(put("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1,\"day_report\": \"" + reportText + "1" + "\", \"date\": \"" + dateNow.toString() + "\"}")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.day_report").value(updateDay.getDay_report()))
                .andExpect(jsonPath("$.date").value(updateDay.getDate().toString()));


        verify(daySheetService, times(1)).updateDay(any(DaySheetDto.class), any(String.class));
    }


    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testGetDayByDate() throws Exception {
        // Arrange
        DaySheetDto getDay = getDaySheetDto();
        when(daySheetService.getDaySheetByDate(any(LocalDate.class), any(String.class))).thenReturn(getDay);

        mockMvc.perform(get("/daysheet/getByDate/" + getDay.getDate().toString())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.day_report").value(getDay.getDay_report()))
                .andExpect(jsonPath("$.date").value(getDay.getDate().toString()));

        verify(daySheetService, times(1)).getDaySheetByDate(any(LocalDate.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testGetDayById() throws Exception {
        // Arrange
        DaySheetDto getDay = getDaySheetDto();
        when(daySheetService.getDaySheetById(any(Long.class), any(String.class))).thenReturn(getDay);

        mockMvc.perform(get("/daysheet/getById/" + getDay.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.day_report").value(getDay.getDay_report()))
                .andExpect(jsonPath("$.date").value(getDay.getDate().toString()));

        verify(daySheetService, times(1)).getDaySheetById(any(Long.class), any(String.class));
    }
}
