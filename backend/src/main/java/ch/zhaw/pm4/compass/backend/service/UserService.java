package ch.zhaw.pm4.compass.backend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.AuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.repository.LocalUserRepository;
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

	public AuthZeroUserDto getUserById(String userID) {
		AuthZeroUserDto authZeroUserDto = null;

		try {
			Request request = new Request.Builder().url(baseUrl + "/api/v2/users/" + userID)
					.addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + getToken()).build();
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				authZeroUserDto = (new Gson()).fromJson(response.body().string(), AuthZeroUserDto.class);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (authZeroUserDto != null) {
			// add role to user
			authZeroUserDto.setRole(getUserRole(authZeroUserDto.getUser_id()));
		}

		return authZeroUserDto;
	}

	public List<AuthZeroUserDto> getAllUsers() {
		List<AuthZeroUserDto> authZeroUserDtos = new ArrayList<>();

		try {
			Request request = new Request.Builder().url(baseUrl + "/api/v2/users")
					.addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + getToken()).build();
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				authZeroUserDtos = (new Gson()).fromJson(response.body().string(),
						new TypeToken<List<AuthZeroUserDto>>() {
						}.getType());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Map<String, UserRole> localUserMap = getAllLocalUsers();
		for (AuthZeroUserDto AuthZeroUserDto : authZeroUserDtos) {
			AuthZeroUserDto.setRole(localUserMap.get(AuthZeroUserDto.getUser_id()));
		}

		return authZeroUserDtos;
	}

	public List<AuthZeroUserDto> getAllParticipants() {
		return getAllUsers().stream().filter(authorizesUserDTO -> authorizesUserDTO.getRole() == UserRole.PARTICIPANT)
				.toList();
	}

	public AuthZeroUserDto createUser(AuthZeroUserDto createUserDto) {
		AuthZeroUserDto authZeroUserDto = null;
		UserRole role = createUserDto.getRole();
		createUserDto.setRole(null);

		try {
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create((new Gson()).toJson(createUserDto, AuthZeroUserDto.class), mediaType);
			Request request = new Request.Builder().url(baseUrl + "/api/v2/users")
					.addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + getToken())
					.method("POST", body).build();
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				authZeroUserDto = (new Gson()).fromJson(response.body().string(), AuthZeroUserDto.class);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (authZeroUserDto != null) {
			// persist user with role
			localUserRepository.save(new LocalUser(authZeroUserDto.getUser_id(), role));
			authZeroUserDto.setRole(role);
		}

		return authZeroUserDto;
	}

	public LocalUser getLocalUser(String id) {
		return localUserRepository.findById(id).orElseThrow();
	}

	private UserRole getUserRole(String id) {
		LocalUser user = localUserRepository.findById(id).orElse(null);
		if (user != null) {
			return user.getRole();
		}
		return null;
	}

	private Map<String, UserRole> getAllLocalUsers() {
		return localUserRepository.findAll().stream().map(localUser -> {
			if (localUser.getRole() == null) {
				localUser.setRole(UserRole.NO_ROLE);
			}
			return localUser;
		}).collect(Collectors.toMap(LocalUser::getId, LocalUser::getRole));
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
