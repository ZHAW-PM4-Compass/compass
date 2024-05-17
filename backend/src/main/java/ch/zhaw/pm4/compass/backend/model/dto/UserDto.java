package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @NonNull
    private List<DaySheetDto> daySheets;

    private UserRole role;
    private Boolean deleted;

    public UserDto(String user_id, String email, String given_name, String family_name, List<DaySheetDto> daySheets, UserRole role) {
        this.user_id = user_id;
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.daySheets = daySheets;
        this.role = role;
        this.deleted = false;
    }

    public UserDto(String user_id, String given_name, String family_name, String email, UserRole role, Boolean deleted) {
        this.user_id = user_id;
        this.given_name = given_name;
        this.family_name = family_name;
        this.email = email;
        this.daySheets = new ArrayList<>();
        this.role = role;
        this.deleted = !isNull(deleted) && deleted;
    }
}
