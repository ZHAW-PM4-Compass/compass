package ch.zhaw.pm4.compass.backend.service;


import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.AuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.CreateAuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.LocalUserRepository;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @InjectMocks
    private UserService userService = spy(new UserService());

    @Mock
    private LocalUserRepository localUserRepository;
    @Mock
    private OkHttpClient client;
    @Mock
    private Call mockCall;
    @Mock
    private Response response;
    @Mock
    private ResponseBody responseBody;

    private UserDto userDto;
    private String authZeroTestToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Im1uSjfhr8h6M3KjcV0u0S-PhSqopm_GSDDrAEZ92EFBUXp7qvLcUJwa_vkPXFIhHB0pX9K3E5wzya2D21KV9FfoUbkw";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockitoAnnotations.openMocks(localUserRepository);
        userDto = getUserDto();

        // User env variables here
        userService.baseUrl = "https://stadtmuur-compass-dev.eu.auth0.com";
        userService.clientId = "statdtmuur";
        userService.clientSecret = "secret";
        userService.audience = "backend";
    }

    private LocalUser getLocalUser() {
        return new LocalUser(getUserDto().getUser_id(), getUserDto().getRole());
    }

    private UserDto getUserDto() {
        return new UserDto("auth0|23sdfyl22ffowqpmclblrtkwerwsdff", "Test", "User", "test.user@stadtmuur.ch", "Participant", false);
    }

    private CreateAuthZeroUserDto getCreateAuthZeroUserDto() {
        return new CreateAuthZeroUserDto(getUserDto().getUser_id(), getUserDto().getEmail(), getUserDto().getGiven_name(), getUserDto().getFamily_name(), getUserDto().getRole(), "Swordfish");
    }

    private AuthZeroUserDto getAuthZeroUserDto() {
        return new AuthZeroUserDto(getUserDto().getEmail(), getUserDto().getGiven_name(), getUserDto().getFamily_name(), getUserDto().getRole(), false);
    }

    @Test
    public void testGetUserById() throws IOException {
        prepareCRUDTest();
        UserDto resultUserDto = userService.getUserById(getUserDto().getUser_id());
        compareUserDto(userDto, resultUserDto);
    }

    @Test
    public void testGetAllUsers() throws IOException {
        ArrayList<CreateAuthZeroUserDto> authZeroUserDtoList = new ArrayList<>();
        authZeroUserDtoList.add(getCreateAuthZeroUserDto());
        ArrayList<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(getUserDto());

        //Auth0
        mockAuthZeroCall();
        doReturn("").when(userService).getToken();
        when(response.body().string()).thenReturn((new Gson()).toJson(authZeroUserDtoList, new TypeToken<List<CreateAuthZeroUserDto>>() {
        }.getType()));

        //Repository
        when(localUserRepository.findById(any(String.class))).thenReturn(Optional.of(getLocalUser()));

        List<UserDto> resultUserDtoList = userService.getAllUsers();

        compareUserDto(userDtoList.getFirst(), resultUserDtoList.getFirst());
    }

    @Test
    public void testGetAllParticipants() throws IOException {
        ArrayList<CreateAuthZeroUserDto> authZeroUserDtoList = new ArrayList<>();
        authZeroUserDtoList.add(getCreateAuthZeroUserDto());
        ArrayList<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(getUserDto());

        //Auth0
        mockAuthZeroCall();
        doReturn("").when(userService).getToken();
        when(response.body().string()).thenReturn((new Gson()).toJson(authZeroUserDtoList, new TypeToken<List<CreateAuthZeroUserDto>>() {
        }.getType()));

        //Repository
        when(localUserRepository.findById(any(String.class))).thenReturn(Optional.of(getLocalUser()));

        List<UserDto> resultUserDtoList = userService.getAllParticipants();

        compareUserDto(userDtoList.getFirst(), resultUserDtoList.getFirst());
    }

    @Test
    public void testGetAllParticipantsNegative() throws IOException {
        ArrayList<CreateAuthZeroUserDto> authZeroUserDtoList = new ArrayList<>();
        authZeroUserDtoList.add(getCreateAuthZeroUserDto());
        ArrayList<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(getUserDto());

        //Auth0
        mockAuthZeroCall();
        doReturn("").when(userService).getToken();
        when(response.body().string()).thenReturn((new Gson()).toJson(authZeroUserDtoList, new TypeToken<List<CreateAuthZeroUserDto>>() {
        }.getType()));

        //Repository
        LocalUser localUser = getLocalUser();
        localUser.setRole("admin");
        when(localUserRepository.findById(any(String.class))).thenReturn(Optional.of(localUser));

        assertEquals(0, userService.getAllParticipants().size());
    }

    @Test
    public void testGetAllLocalUser() {
        ArrayList<LocalUser> localUsers = new ArrayList<>();
        localUsers.add(getLocalUser());

        when(localUserRepository.findAll()).thenReturn(localUsers);
        Map<String, String> mapOfLocalUsers = userService.getAllLocalUsers();

        assertEquals(localUsers.get(0).getRole(), mapOfLocalUsers.get(localUsers.get(0).getId()));
    }

    @Test
    public void testCreateUser() throws IOException {
        prepareCRUDTest();
        UserDto resultUserDto = userService.createUser(getCreateAuthZeroUserDto());
        compareUserDto(userDto, resultUserDto);
    }

    @Test
    public void testUpdateUser() throws IOException {
        prepareCRUDTest();
        UserDto resultUserDto = userService.updateUser(userDto.getUser_id(), getAuthZeroUserDto());
        compareUserDto(userDto, resultUserDto);
    }

    @Test
    public void testGetToken() throws IOException {
        //Auth0
        mockAuthZeroCall();
        when(response.body().string()).thenReturn("{ \"access_token\" : \"" + authZeroTestToken + "\"}");

        assertEquals(authZeroTestToken, userService.getToken());
    }

    @Test
    public void testMapAuthZeroUserToUserDto() {
        AuthZeroUserDto authZeroUserDto = new AuthZeroUserDto(userDto.getEmail(), userDto.getGiven_name(), userDto.getFamily_name(), userDto.getRole(), userDto.getDeleted());
        UserDto resultUserDto = userService.mapAuthZeroUserToUserDto(userDto.getUser_id(), authZeroUserDto);

        compareUserDto(userDto, resultUserDto);
    }

    @Test
    public void prepareCRUDTest() throws IOException {
        //Auth0
        mockAuthZeroCall();
        doReturn("").when(userService).getToken();
        when(response.body().string()).thenReturn((new Gson()).toJson(getUserDto(), UserDto.class));

        //Repository
        when(localUserRepository.findById(any(String.class))).thenReturn(Optional.of(getLocalUser()));
    }

    @Test
    public void testDeleteUser() throws IOException {
        prepareCRUDTest();
        UserDto resultUserDto = userService.deleteUser(userDto.getUser_id());
        compareUserDto(userDto, resultUserDto);
    }

    @Test
    public void restoreUser() throws IOException {
        prepareCRUDTest();
        UserDto resultUserDto = userService.restoreUser(userDto.getUser_id());
        compareUserDto(userDto, resultUserDto);
    }

    @Test
    public void testGetUserRole() {
        when(localUserRepository.findById(any(String.class))).thenReturn(Optional.of(getLocalUser()));
        assertEquals(userService.getUserRole(getUserDto().getUser_id()), userDto.getRole());
    }

    private void compareUserDto(UserDto firstDto, UserDto secondDto) {
        assertEquals(firstDto.getUser_id(), secondDto.getUser_id());
        assertEquals(firstDto.getEmail(), secondDto.getEmail());
        assertEquals(firstDto.getGiven_name(), secondDto.getGiven_name());
        assertEquals(firstDto.getFamily_name(), secondDto.getFamily_name());
        assertEquals(firstDto.getRole(), secondDto.getRole());
        assertEquals(firstDto.getDeleted(), secondDto.getDeleted());
    }


    private void mockAuthZeroCall() throws IOException {
        when(client.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(response);
        when(response.body()).thenReturn(responseBody);
        when(response.isSuccessful()).thenReturn(true);
    }
}