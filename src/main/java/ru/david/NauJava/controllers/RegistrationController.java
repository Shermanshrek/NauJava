package ru.david.NauJava.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.david.NauJava.entity.User;
import ru.david.NauJava.services.UserService;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            Model model
    ) {
        try{
            User user = new User(username, password, firstName, lastName);
            userService.addUser(user);
            return "redirect:/login";
        } catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
    }
}
