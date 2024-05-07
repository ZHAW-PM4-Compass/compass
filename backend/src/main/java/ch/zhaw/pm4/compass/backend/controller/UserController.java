package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.AuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.CreateAuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
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
    public ResponseEntity<UserDto> createUser(@RequestBody CreateAuthZeroUserDto userDto) {
        UserDto queryUser = userService.createUser(userDto);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<UserDto> deleteUser(@PathVariable String id) {
        UserDto queryUser = userService.deleteUser(id);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PutMapping(path = "update/{id}", produces = "application/json")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody AuthZeroUserDto userDto) {
        UserDto queryUser = userService.updateUser(id, userDto);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PutMapping(path = "restore/{id}", produces = "application/json")
    public ResponseEntity<UserDto> restoreUser(@PathVariable String id) {
        UserDto queryUser = userService.restoreUser(id);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(path = "getById/{id}", produces = "application/json")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        UserDto queryUser = userService.getUserById(id);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(path = "getAllUsers", produces = "application/json")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> queryUsers = userService.getAllUsers();
        if (queryUsers != null) {
            return ResponseEntity.ok(queryUsers);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(path = "getAllParticipants", produces = "application/json")
    public ResponseEntity<List<UserDto>> getAllParticipants() {
        List<UserDto> queryUsers = userService.getAllParticipants();
        if (queryUsers != null) {
            return ResponseEntity.ok(queryUsers);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}