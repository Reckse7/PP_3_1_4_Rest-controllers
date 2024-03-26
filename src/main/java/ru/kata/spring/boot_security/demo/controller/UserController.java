package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {

    private UserService service;

    @Autowired
    public void setUserService(UserService service) {
        this.service = service;
    }

    @GetMapping("/user")
    public String userPage(ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("authUser", authentication.getPrincipal());
        return "user";
    }

    @GetMapping("/admin")
    public String printUsers(ModelMap model) {
        List<User> list = service.getAllUsers();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("users", list);
        model.addAttribute("authUser", authentication.getPrincipal());
        return "admin";
    }

    @PostMapping("/admin/save")
    public String editUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                           @ModelAttribute("role") Role role) {
        if (bindingResult.hasErrors()) {
                return "redirect:/admin";
        }
        service.save(user, role);
        return "redirect:/admin";
    }

    @GetMapping("/admin/delete")
    public String deleteUser(@RequestParam int id) {
        service.delete(id);
        return "redirect:/admin";
    }
}
