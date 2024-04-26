package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Controller", description = "User Endpoint")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(produces = "application/json")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping(path = "getById/{id}", produces = "application/json")
    public UserDto getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping(path = "getAll", produces = "application/json")
    public List<UserDto> getAll() {
        return userService.getAllUsers();
    }
}