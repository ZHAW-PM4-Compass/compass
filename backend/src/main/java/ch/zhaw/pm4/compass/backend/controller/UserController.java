package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.FullUserDto;
import ch.zhaw.pm4.compass.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Controller", description = "User Endpoint")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<FullUserDto> createUser(@RequestBody FullUserDto userDto) {
        FullUserDto queryUser = userService.createUser(userDto);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(path = "getById/{id}", produces = "application/json")
    public ResponseEntity<FullUserDto> getUserById(@PathVariable String id) {
        FullUserDto queryUser = userService.getUserById(id);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(path = "getAll", produces = "application/json")
    public ResponseEntity<List<FullUserDto>> getAll() {
        List<FullUserDto> queryUsers = userService.getAllUsers();
        if (queryUsers != null) {
            return ResponseEntity.ok(queryUsers);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}