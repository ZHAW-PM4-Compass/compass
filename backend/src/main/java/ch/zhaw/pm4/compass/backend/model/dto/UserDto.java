package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.Data;
import lombok.NonNull;

import static java.util.Objects.isNull;

@Data
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
  private Boolean deleted;

    public UserDto(String email, String given_name, String family_name, UserRole role, String user_id) {
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.role = role;
        this.user_id = user_id;
        this.deleted = false;
    }

    public UserDto(String user_id, String given_name, String family_name, String email, UserRole role, Boolean deleted) {
        this.user_id = user_id;
        this.given_name = given_name;
        this.family_name = family_name;
        this.email = email;
        this.role = role;
        this.deleted = !isNull(deleted) && deleted;
    }
}
