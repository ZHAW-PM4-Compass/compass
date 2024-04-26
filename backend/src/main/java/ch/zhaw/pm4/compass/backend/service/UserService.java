package ch.zhaw.pm4.compass.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;

import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import jakarta.annotation.PostConstruct;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class UserService {
	@Autowired
	private Environment env;
	private String baseUrl;
	private String clientId;
	private String clientSecret;
	private String audience;
	private OkHttpClient client = new OkHttpClient().newBuilder().build();

	@PostConstruct
	public void init() {
		baseUrl = env.getProperty("auth0.mgmt.baseurl");
		clientId = env.getProperty("auth0.mgmt.clientId");
		clientSecret = env.getProperty("auth0.mgmt.clientSecret");
		audience = env.getProperty("auth0.mgmt.audience");
	}

	public UserDto getUserById(String userID) {
		UserDto userDto = null;

		try {
			Request request = new Request.Builder().url(baseUrl + "/api/v2/users/" + userID)
					.addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + getToken()).build();
			Response response = client.newCall(request).execute();
			userDto = (new Gson()).fromJson(response.body().string(), UserDto.class);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return userDto;
	}

	public List<UserDto> getAllUsers() {
		List<UserDto> userDtos = new ArrayList<>();

		try {
			Request request = new Request.Builder().url(baseUrl + "/api/v2/users")
					.addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + getToken()).build();
			Response response = client.newCall(request).execute();
			userDtos = (new Gson()).fromJson(response.body().string(), new TypeToken<List<UserDto>>() {
			}.getType());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return userDtos;
	}

	public UserDto createUser(UserDto user) {
		UserDto responseUserDto = null;

		try {
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, (new Gson()).toJson(user));
			Request request = new Request.Builder().url(baseUrl + "/api/v2/users")
					.addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + getToken())
					.method("POST", body).build();
			Response response = client.newCall(request).execute();
			responseUserDto = (new Gson()).fromJson(response.body().string(), UserDto.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return responseUserDto;
	}

	private String getToken() {
		String token = "";

		try {
			RequestBody body = new FormBody.Builder().addEncoded("grant_type", "client_credentials")
					.addEncoded("client_id", clientId).addEncoded("client_secret", clientSecret)
					.addEncoded("audience", audience).build();

			Request request = new Request.Builder().url(baseUrl + "/oauth/token")
					.addHeader("content-type", "application/x-www-form-urlencoded").post(body).build();

			Response response = client.newCall(request).execute();
			token = response.body().string();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		JsonObject jsonObject = JsonParser.parseString(token).getAsJsonObject();
		return jsonObject.get("access_token").getAsString();
	}
}
