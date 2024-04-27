package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.model.CompassUser;
import ch.zhaw.pm4.compass.backend.model.dto.FullUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.CompassUserRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private Environment env;
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String audience;

    @Autowired
    CompassUserRepository compassUserRepository;

    private OkHttpClient client = new OkHttpClient().newBuilder().build();

    @PostConstruct
    public void init() {
        baseUrl = env.getProperty("auth0.mgmt.baseurl");
        clientId = env.getProperty("auth0.mgmt.clientId");
        clientSecret = env.getProperty("auth0.mgmt.clientSecret");
        audience = env.getProperty("auth0.mgmt.audience");
    }

    public FullUserDto getUserById(String userID) {
        FullUserDto fullUserDto = null;

        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users/" + userID)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                fullUserDto = (new Gson()).fromJson(response.body().string(), FullUserDto.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (fullUserDto != null) {
            //add role to user
            fullUserDto.setRole(getUserRole(fullUserDto.getUser_id()));
        }

        return fullUserDto;
    }

    public List<FullUserDto> getAllUsers() {
        List<FullUserDto> fullUserDtos = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                fullUserDtos = (new Gson()).fromJson(response.body().string(), new TypeToken<List<FullUserDto>>() {
                }.getType());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (FullUserDto fullUserDto : fullUserDtos) {
            fullUserDto.setRole(getUserRole(fullUserDto.getUser_id()));
        }

        return fullUserDtos;
    }

    public FullUserDto createUser(FullUserDto createUserDto) {
        FullUserDto fullUserDto = null;

        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, (new Gson()).toJson(createUserDto, UserDto.class));
            Request request = new Request.Builder()
                    .url(baseUrl + "/api/v2/users")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                fullUserDto = (new Gson()).fromJson(response.body().string(), FullUserDto.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (fullUserDto != null) {
            //persist user with role
            compassUserRepository.save(new CompassUser(fullUserDto.getUser_id(), createUserDto.getRole()));
            fullUserDto.setRole(createUserDto.getRole());
        }

        return fullUserDto;
    }

    private String getUserRole(String id) {
        CompassUser user = compassUserRepository.findById(id).orElse(null);
        if (user != null) {
            return user.getRole();
        }
        return null;
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
