package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthZeroUserDto {
    private String email;
    private String given_name;
    private String family_name;
    private String role;
    private Boolean blocked;

    public AuthZeroUserDto() {

    }

    public AuthZeroUserDto(String email, String given_name, String family_name, String role) {
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.role = role;
        this.blocked = false;
    }

    public AuthZeroUserDto(String email, String given_name, String family_name, String role, Boolean blocked) {
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.role = role;
        this.blocked = blocked;
    }

}
