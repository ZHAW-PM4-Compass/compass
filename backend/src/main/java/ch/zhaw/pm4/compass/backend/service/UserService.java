package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.CreateAuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateAuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.repository.LocalUserRepository;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private Environment env;
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String audience;

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

    public CreateAuthZeroUserDto getUserById(String userID) {
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

        return createAuthZeroUserDto;
    }

    public List<CreateAuthZeroUserDto> getAllUsers() {
        List<CreateAuthZeroUserDto> createAuthZeroUserDtos = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                createAuthZeroUserDtos = (new Gson()).fromJson(response.body().string(), new TypeToken<List<CreateAuthZeroUserDto>>() {
                }.getType());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> localUserMap = getAllLocalUsers();
        for (CreateAuthZeroUserDto CreateAuthZeroUserDto : createAuthZeroUserDtos) {
            CreateAuthZeroUserDto.setRole(localUserMap.get(CreateAuthZeroUserDto.getUser_id()));
        }

        return createAuthZeroUserDtos;
    }

    public List<CreateAuthZeroUserDto> getAllParticipants() {
        return getAllUsers().stream().filter(authorizesUserDTO -> "Participant".equals(authorizesUserDTO.getRole())).toList();
    }

    public CreateAuthZeroUserDto createUser(CreateAuthZeroUserDto createUserDto) {
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
        }

        return createAuthZeroUserDto;
    }

    private String getUserRole(String id) {
        LocalUser user = localUserRepository.findById(id).orElse(null);
        if (user != null) {
            return user.getRole();
        }
        return null;
    }

    public UpdateAuthZeroUserDto updateUser(String userId, UpdateAuthZeroUserDto updateUserDto) {
        UpdateAuthZeroUserDto updateAuthZeroUserDto = null;
        String role = updateUserDto.getRole();
        updateUserDto.setRole(null);

        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, (new Gson()).toJson(updateUserDto, UpdateAuthZeroUserDto.class));
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users/" + userId)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .addHeader("Content-Type", "application/json")
                    .method("PATCH", body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                updateAuthZeroUserDto = (new Gson()).fromJson(response.body().string(), UpdateAuthZeroUserDto.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (updateAuthZeroUserDto != null) {
            //persist user with role
            localUserRepository.save(new LocalUser(userId, role));
            updateAuthZeroUserDto.setRole(role);
        }

        return updateAuthZeroUserDto;
    }



    public boolean deleteUser(String userId) {
        boolean successful = false;
        try {
            MediaType mediaType = MediaType.parse("application/json");
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users/" + userId)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .addHeader("Content-Type", "application/json")
                    .delete()
                    .build();
            Response response = client.newCall(request).execute();
            successful = response.isSuccessful();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (successful) {
            //persist user with role
            LocalUser localUser = localUserRepository.findById(userId).orElse(null);
            if (localUser != null && !localUser.isEmpty()) localUserRepository.delete(localUser);
        }

        return successful;
    }

    private Map<String, String> getAllLocalUsers() {
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

    private String getToken() {
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
}
