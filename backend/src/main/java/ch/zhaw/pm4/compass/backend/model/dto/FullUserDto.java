package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullUserDto extends UserDto {

    private String role;

    public FullUserDto(String email, String given_name, String family_name, String role, String password, String connection, String user_id) {
        super(email, given_name, family_name, password, connection, user_id);
        this.role = role;

    }
}
