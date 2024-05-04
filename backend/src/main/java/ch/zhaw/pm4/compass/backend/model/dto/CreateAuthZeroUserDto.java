package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuthZeroUserDto extends UserDto {

    private String password;
    private String connection;

    public CreateAuthZeroUserDto(String email, String given_name, String family_name, String role, String password, String user_id) {
        super(email, given_name, family_name, role, user_id);
        this.password = password;
        this.connection = "Username-Password-Authentication";
    }
}
