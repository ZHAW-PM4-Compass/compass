package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuthZeroUserDto extends AuthZeroUserDto {
	private String user_id;
	private String password;
	private String connection;

	public CreateAuthZeroUserDto(String user_id, String email, String given_name, String family_name, UserRole role,
			String password) {
		super(email, given_name, family_name, role);
		this.user_id = user_id;
		this.password = password;
		this.connection = "Username-Password-Authentication";
	}
}
