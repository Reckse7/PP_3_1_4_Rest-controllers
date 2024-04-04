package ru.kata.spring.boot_security.demo.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    void save(User user);
    void delete(int id);
    User getById(int id);
    List<User> getAllUsers();
    Optional<User> getByEmail(String email);
}
