package ru.kata.spring.boot_security.demo.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserErrorResponse;
import ru.kata.spring.boot_security.demo.util.UserNotSavedException;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    private final UserService service;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService service, ModelMapper modelMapper, PasswordEncoder encoder, UserValidator userValidator) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.userValidator = userValidator;
    }

    @GetMapping("/user")
    public UserDTO userPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return convertToUserDTO((User) authentication.getPrincipal());
    }

    @GetMapping("/user/{id}")
    public UserDTO getUser(@PathVariable int id) {
        return convertToUserDTO(service.getById(id));
    }

    @GetMapping("/admin")
    public List<UserDTO> getUserList() {
        return service.getAllUsers().stream().map(this::convertToUserDTO).toList();
    }

    @PostMapping("/admin/save")
    public ResponseEntity<HttpStatus> saveUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        userValidator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errorMassage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMassage.append(error.getField()).append(": ")
                        .append(error.getDefaultMessage()).append("; ");
            }
            throw new UserNotSavedException(errorMassage.toString());
        }
        userDTO.setUserPassword(encoder.encode(userDTO.getUserPassword()));
        service.save(convertToUser(userDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotSavedException e) {
        UserErrorResponse errorResponse = new UserErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
