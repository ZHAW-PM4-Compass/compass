package ch.zhaw.pm4.compass.backend.model.dto;

public class AuthZeroUserDto extends UserDto {

    private String password;
    private String connection;

    public AuthZeroUserDto(String email, String given_name, String family_name, String role, String password, String user_id) {
        super(email, given_name, family_name, role, user_id);
        this.password = password;
        this.connection = "Username-Password-Authentication";
    }
}
