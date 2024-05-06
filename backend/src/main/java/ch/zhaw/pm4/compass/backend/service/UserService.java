package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.AuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.CreateAuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.LocalUserRepository;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private Environment env;
    public String baseUrl;
    public String clientId;
    public String clientSecret;
    public String audience;

    @Autowired
    LocalUserRepository localUserRepository;

    private OkHttpClient client = new OkHttpClient().newBuilder().build();

    @PostConstruct
    public void init() {
        baseUrl = env.getProperty("auth0.mgmt.baseurl");
        clientId = env.getProperty("auth0.mgmt.clientId");
        clientSecret = env.getProperty("auth0.mgmt.clientSecret");
        audience = env.getProperty("auth0.mgmt.audience");
    }

    public UserDto getUserById(String userID) {
        CreateAuthZeroUserDto createAuthZeroUserDto = null;

        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users/" + userID)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                createAuthZeroUserDto = (new Gson()).fromJson(response.body().string(), CreateAuthZeroUserDto.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (createAuthZeroUserDto != null) {
            //add role to user
            createAuthZeroUserDto.setRole(getUserRole(createAuthZeroUserDto.getUser_id()));
        }

        return mapAuthZeroUserToUserDto(userID, createAuthZeroUserDto);
    }

    public List<UserDto> getAllUsers() {
        List<CreateAuthZeroUserDto> authZeroUserDtos = new ArrayList<>();
        List<UserDto> userDtos = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                authZeroUserDtos = (new Gson()).fromJson(response.body().string(), new TypeToken<List<CreateAuthZeroUserDto>>() {
                }.getType());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (CreateAuthZeroUserDto CreateAuthZeroUserDto : authZeroUserDtos) {
            LocalUser localUser = localUserRepository.findById(CreateAuthZeroUserDto.getUser_id()).orElse(null);
            if (localUser != null && !localUser.isEmpty()) {
                CreateAuthZeroUserDto.setRole(localUser.getRole());
                userDtos.add(mapAuthZeroUserToUserDto(CreateAuthZeroUserDto.getUser_id(), CreateAuthZeroUserDto));
            }
        }

        return userDtos;
    }

    public List<UserDto> getAllParticipants() {
        return getAllUsers().stream().filter(authorizesUserDTO -> "Participant".equals(authorizesUserDTO.getRole())).toList();
    }

    public UserDto createUser(CreateAuthZeroUserDto createUserDto) {
        CreateAuthZeroUserDto createAuthZeroUserDto = null;
        String role = createUserDto.getRole();
        createUserDto.setRole(null);

        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, (new Gson()).toJson(createUserDto, CreateAuthZeroUserDto.class));
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                createAuthZeroUserDto = (new Gson()).fromJson(response.body().string(), CreateAuthZeroUserDto.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (createAuthZeroUserDto != null) {
            //persist user with role
            localUserRepository.save(new LocalUser(createAuthZeroUserDto.getUser_id(), role));
            createAuthZeroUserDto.setRole(role);
            return mapAuthZeroUserToUserDto(createAuthZeroUserDto.getUser_id(), createAuthZeroUserDto);
        }
        return null;
    }

    public String getUserRole(String id) {
        Optional<LocalUser> user = localUserRepository.findById(id);
        if (user.isPresent()) {
            return user.get().getRole();
        }
        return null;
    }

    public UserDto updateUser(String userId, AuthZeroUserDto updateUserDto) {
        AuthZeroUserDto authZeroUserDto = null;
        String role = updateUserDto.getRole();
        updateUserDto.setRole(null);

        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, (new Gson()).toJson(updateUserDto, AuthZeroUserDto.class));
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users/" + userId)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .addHeader("Content-Type", "application/json")
                    .method("PATCH", body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                authZeroUserDto = (new Gson()).fromJson(response.body().string(), AuthZeroUserDto.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (authZeroUserDto != null) {
            //persist user with role
            localUserRepository.save(new LocalUser(userId, role));
            authZeroUserDto.setRole(role);
        }

        return mapAuthZeroUserToUserDto(userId, authZeroUserDto);
    }

    public UserDto deleteUser(String userId) {
        UserDto currentUser = getUserById(userId);
        if (currentUser == null) return null;

        AuthZeroUserDto updateUserDto = new AuthZeroUserDto(
                currentUser.getEmail(),
                currentUser.getGiven_name(),
                currentUser.getFamily_name(),
                currentUser.getRole(),
                true
        );

        return updateUser(userId, updateUserDto);
    }

    public UserDto restoreUser(String userId) {
        UserDto currentUser = getUserById(userId);
        if (currentUser == null) return null;

        AuthZeroUserDto updateUserDto = new AuthZeroUserDto(
                currentUser.getEmail(),
                currentUser.getGiven_name(),
                currentUser.getFamily_name(),
                currentUser.getRole(),
                false
        );

        return updateUser(userId, updateUserDto);
    }

    public Map<String, String> getAllLocalUsers() {
        return localUserRepository.findAll().stream()
                .map(localUser -> {
                    if(localUser.getRole() == null ) {
                        localUser.setRole("Keine Rolle");
                    }
                    return  localUser;
                })
                .collect(Collectors.toMap(
                        LocalUser::getId,
                        LocalUser::getRole));
    }

    public String getToken() {
        String token = "";

        try {
            RequestBody body = new FormBody.Builder()
                    .addEncoded("grant_type", "client_credentials")
                    .addEncoded("client_id", clientId)
                    .addEncoded("client_secret", clientSecret)
                    .addEncoded("audience", audience)
                    .build();

            Request request = new Request.Builder()
                    .url(baseUrl + "/oauth/token")
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                token = response.body().string();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonObject jsonObject = JsonParser.parseString(token).getAsJsonObject();
        return jsonObject.get("access_token").getAsString();
    }

    public UserDto mapAuthZeroUserToUserDto(String userId, AuthZeroUserDto authZeroUserDto) {
        if (authZeroUserDto == null) return null;
        return new UserDto(
                userId,
                authZeroUserDto.getGiven_name(),
                authZeroUserDto.getFamily_name(),
                authZeroUserDto.getEmail(),
                authZeroUserDto.getRole(),
                authZeroUserDto.getBlocked()
        );
    }
}
