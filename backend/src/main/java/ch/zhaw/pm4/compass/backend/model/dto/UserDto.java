package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.*;

import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}
