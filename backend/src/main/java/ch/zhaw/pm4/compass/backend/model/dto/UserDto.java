package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import static java.util.Objects.isNull;

@Getter
@Setter
public class UserDto {
    private String email;
    private String given_name;
    private String family_name;
    private String role;
    private String user_id;
    private Boolean deleted;

    public UserDto(String email, String given_name, String family_name, String role, String user_id) {
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.role = role;
        this.user_id = user_id;
        this.deleted = false;
    }

    public UserDto(String user_id, String given_name, String family_name, String email, String role, Boolean deleted) {
        this.user_id = user_id;
        this.given_name = given_name;
        this.family_name = family_name;
        this.email = email;
        this.role = role;
        this.deleted = !isNull(deleted) && deleted;
    }
}
