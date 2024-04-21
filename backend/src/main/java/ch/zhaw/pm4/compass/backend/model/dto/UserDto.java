package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String given_name;
    private String family_name;
    private String role;
    private String password;
    private String connection;
    private String user_id;

    public UserDto(String email, String given_name, String family_name, String role, String password, String connection, String user_id) {
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.role = role;
        this.password = password;
        this.connection = connection;
        this.user_id = user_id;
    }
}
