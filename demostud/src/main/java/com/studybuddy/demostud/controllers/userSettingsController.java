package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.Service.UserSettingsService;
import com.studybuddy.demostud.models.UserSettings;
import org.springframework.beans.factory.annotation.Autowired;
import com.studybuddy.demostud.Service.UserServiceWork;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings")
public class userSettingsController {


    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private UserServiceWork userServiceWork;


    @GetMapping("/{id}")
    public String getSettings(@PathVariable Long id, Model model) {
        UserSettings settings = userSettingsService.getUserSettings(id).orElse(new UserSettings());


    String currentUsername = UserServiceWork.getUsernameById(id);

        model.addAttribute("settings", settings);
        model.addAttribute("currentUsername", currentUsername);

        return "settings";
}

    @PostMapping("/{id}")
    public String saveSettings(@PathVariable Long id, @ModelAttribute UserSettings settings) {
        settings.setId(id);
        userSettingsService.saveUserSettings(settings);
        return "redirect:/settings/" + id;
    }
}