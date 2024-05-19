package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemStatusDto {
    private String commitId;
    private boolean backendIsReachable;
    private boolean databaseIsReachable;
    private boolean auth0IsReachable;
}
