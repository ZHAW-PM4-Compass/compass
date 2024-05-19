package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.SystemStatusDto;
import ch.zhaw.pm4.compass.backend.service.SystemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "System Controller", description = "System Endpoint")
@RestController
@RequestMapping("/system")
public class SystemController {
    @Autowired
    private SystemService systemService;

    @Value("${git.commit.id}")
    private String commitId;

    @Value("${git.commit.time}")
    private String commitTime;

    @GetMapping("/status")
    public ResponseEntity<SystemStatusDto> getStatus() {
        boolean backendIsReachable = systemService.isBackendReachable();
        boolean databaseIsReachable = systemService.isDatabaseReachable();
        boolean auth0IsReachable = systemService.isAuth0Reachable();

        return ResponseEntity.ok(new SystemStatusDto(commitId, commitTime, backendIsReachable, databaseIsReachable, auth0IsReachable));
    }
}
