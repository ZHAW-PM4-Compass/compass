package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAuthZeroUserDto {
    private String email;
    private String given_name;
    private String family_name;
    private String role;

    public UpdateAuthZeroUserDto(String email, String given_name, String family_name, String role) {
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.role = role;
    }
}
