package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDaySheetDayNotesDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
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
                        .content("{\"day_notes\": \"" + reportText + "\", \"date\": \"" + dateNow.toString() + "\"}").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.day_notes").value(reportText))
                .andExpect(jsonPath("$.date").value(dateNow.toString()));


        verify(daySheetService, times(1)).createDay(any(DaySheetDto.class), any(String.class));
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
        when(daySheetService.createDay(any(DaySheetDto.class),any(String.class))).thenReturn(null);


        mockMvc.perform(post("/daysheet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"day_notes\": \"" + reportText + "\", \"date\": \"" + dateNow.toString() + "\"}")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());


        verify(daySheetService, times(1)).createDay(any(DaySheetDto.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testUpdateDayNotes() throws Exception {
        // Arrange



        DaySheetDto updateDay = getUpdateDaySheet();
        updateDay.setConfirmed(false);
        when(daySheetService.updateDayNotes(any(UpdateDaySheetDayNotesDto.class), any(String.class))).thenReturn(updateDay);


        //Act
        mockMvc.perform(put("/daysheet/updateDayNotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1,\"day_notes\": \"" + reportText + "1" + "\", \"date\": \"" + dateNow.toString() + "\", \"confirmed\": \"true\" }")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.day_notes").value(updateDay.getDay_notes()))
                .andExpect(jsonPath("$.date").value(updateDay.getDate().toString()))
                .andExpect(jsonPath("$.confirmed").value(updateDay.getConfirmed().toString()));

        verify(daySheetService, times(1)).updateDayNotes(any(UpdateDaySheetDayNotesDto.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {})
    void testUpdateConfirmed() throws Exception {
        // Arrange

        DaySheetDto updateDay = getUpdateDaySheet();
        updateDay.setConfirmed(true);
        when(daySheetService.updateConfirmed(any(Long.class), any(String.class))).thenReturn(updateDay);


        //Act
        mockMvc.perform(put("/daysheet/confirm/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.confirmed").value(updateDay.getConfirmed().toString()));

        verify(daySheetService, times(1)).updateConfirmed(any(Long.class), any(String.class));
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
                .andExpect(jsonPath("$.day_notes").value(getDay.getDay_notes()))
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
                .andExpect(jsonPath("$.day_notes").value(getDay.getDay_notes()))
                .andExpect(jsonPath("$.date").value(getDay.getDate().toString()));

        verify(daySheetService, times(1)).getDaySheetById(any(Long.class), any(String.class));
    }
}
