package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthZeroUserDto extends UserDto {

	private String password;
	private String connection;

	public AuthZeroUserDto(String email, String given_name, String family_name, UserRole role, String password,
			String user_id) {
		super(email, given_name, family_name, user_id, role);
		this.password = password;
		this.connection = "Username-Password-Authentication";
	}
}
