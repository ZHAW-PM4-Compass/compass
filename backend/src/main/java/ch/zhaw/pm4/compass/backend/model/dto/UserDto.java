package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class UserDto {
	@NonNull
	private String email;
	@NonNull
	private String given_name;
	@NonNull
	private String family_name;
	@NonNull
	private String user_id;

	private UserRole role;
}
