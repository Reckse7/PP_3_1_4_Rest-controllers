package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
        //extends UserDetailsService {
    void save(User user);
    void delete(int id);
    User getById(int id);
    List<User> getAllUsers();
}
