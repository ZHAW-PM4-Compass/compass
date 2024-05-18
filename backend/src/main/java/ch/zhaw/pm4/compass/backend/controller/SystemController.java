package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.SystemStatusDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class SystemController {

    @Value("${git.commit.id}")
    private String commitId;

    @Value("${git.commit.time}")
    private String commitTime;

    @GetMapping("/status")
    public ResponseEntity<SystemStatusDto> getStatus() {
        return ResponseEntity.ok(new SystemStatusDto(commitId, commitTime));
    }
}
