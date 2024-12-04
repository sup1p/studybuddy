package com.studybuddy.demostud.controllers;

import com.studybuddy.demostud.DTOs.*;
import com.studybuddy.demostud.Service.AvatarService;
import com.studybuddy.demostud.Service.LanguageService;
import com.studybuddy.demostud.models.Language;
import com.studybuddy.demostud.models.User;
import com.studybuddy.demostud.models.disciplines_package.SubDiscipline;
import com.studybuddy.demostud.models.disciplines_package.UserSubDiscipline;
import com.studybuddy.demostud.repository.DissciplineRepostory.SubDisciplineRepository;
import com.studybuddy.demostud.repository.DissciplineRepostory.UserSubDisciplineRepository;
import com.studybuddy.demostud.repository.LanguageRepository;
import com.studybuddy.demostud.repository.UserRepository;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final LanguageService languageService;
    private final SubDisciplineRepository subDisciplineRepository;
    private final UserRepository userRepository;
    private final UserSubDisciplineRepository userSubDisciplineRepository;
    private final LanguageRepository languageRepository;
    private final AvatarService avatarService;

    public ProfileController(LanguageService languageService, UserRepository userRepository,
                             UserSubDisciplineRepository userSubDisciplineRepository,
                             SubDisciplineRepository subDisciplineRepository, LanguageRepository languageRepository, AvatarService avatarService) {
        this.languageService = languageService;
        this.userRepository = userRepository;
        this.userSubDisciplineRepository = userSubDisciplineRepository;
        this.subDisciplineRepository = subDisciplineRepository;
        this.languageRepository = languageRepository;
        this.avatarService = avatarService;
    }

    // Helper method to get the authenticated user
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    // Combined GetMapping for Username,About and Languages
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(HttpServletRequest request) {
        User user = getAuthenticatedUser();
        Map<String, Object> response = new HashMap<>();


        // Добавление данных пользователя в ответ
        response.put("username", user.getUsername());
        response.put("about", user.getAbout());
        response.put("languages", languageService.getLanguages(user.getId()));

        return ResponseEntity.ok(response);
    }


    // Avatar Upload
    @PostMapping("/avatar/upload")
    public ResponseEntity<Map<String, Object>> uploadAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        User user = getAuthenticatedUser();
        Map<String, Object> response = new HashMap<>();
        try {
            String avatarUrl = avatarService.uploadAvatar(user.getId(), file);
            response.put("message", "Avatar uploaded successfully");
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Failed to upload avatar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/avatar/update")
    public ResponseEntity<Map<String, Object>> updateAvatar(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        User user = getAuthenticatedUser();
        Map<String, Object> response = new HashMap<>();
        try {
            String avatarUrl = avatarService.updateAvatar(user.getId(), file);
            response.put("message", "Avatar updated successfully");
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Failed to update avatar");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/avatar/my-get")
    public ResponseEntity<Map<String, Object>> getMyAvatar(HttpServletRequest request) {
        User user = getAuthenticatedUser();
        Map<String, Object> response = new HashMap<>();
        try {
            Resource file = avatarService.getAvatar(user.getId());
            response.put("avatarUrl", user.getAvatarPath());
            response.put("message", "Avatar retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Avatar not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/avatar/other-get")
    public ResponseEntity<Map<String, Object>> getAvatarById(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Resource file = avatarService.getAvatar(userId);
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                response.put("avatarUrl", userOptional.get().getAvatarPath());
                response.put("message", "Avatar retrieved successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IOException e) {
            response.put("message", "Avatar not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @GetMapping("/all-usernames")
    public ResponseEntity<List<UsernameResponse>> getAllUsernames() {
        User user = getAuthenticatedUser(); // получаем текущего пользователя
        List<User> users = userRepository.findAll(); // получение всех пользователей из базы данных
        List<UsernameResponse> Usernames = users.stream()
                .map(username -> new UsernameResponse(username.getId(), username.getUsername())) // преобразование списка пользователей в список никнеймов
                .collect(Collectors.toList());
        return ResponseEntity.ok(Usernames);
    }

    // About editing
    @PutMapping("/about/edit")
    public ResponseEntity<String> updateUserAbout(@RequestBody Map<String, String> updatedAbout) {
        User user = getAuthenticatedUser();
        String about = updatedAbout.get("about");
        if (about == null || about.isBlank()) {
            return new ResponseEntity<>("Поле 'about' не может быть пустым.", HttpStatus.BAD_REQUEST);
        }

        user.setAbout(about);
        userRepository.save(user);
        return new ResponseEntity<>("Биография пользователя обновлена успешно.", HttpStatus.OK);
    }

    @GetMapping("/language/all")
    public ResponseEntity<List<LanguagesResponse>> language() {
        User user = getAuthenticatedUser();
        List<Language> languages = languageRepository.findAll();
        List<LanguagesResponse> response = languages.stream()
                .map(language ->
                new LanguagesResponse(language.getId(), language.getLanguageName())
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Languages adding
    @PostMapping("/language/add")
    public ResponseEntity<String> addLanguageToUser(@RequestBody Language language) {
        User user = getAuthenticatedUser();
        try {
            languageService.addLanguageToUser(user.getId(), language.getLanguageName());
            return ResponseEntity.ok("Язык успешно добавлен пользователю!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }
    //language deleting
    @DeleteMapping("/language/delete")
    public ResponseEntity<String> deleteLanguage(@RequestBody Map<String, String> requestBody) {
        User user = getAuthenticatedUser();
        try {
            String languageName = requestBody.get("languageName");
            if (languageName == null) {
                throw new IllegalArgumentException("Language name is missing");
            }
            languageService.deleteLanguageFromUser(user.getId(), languageName);
            return ResponseEntity.ok("Язык успешно удалён!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: " + e.getMessage());
        }
    }

    // return user's academic skills(disciplines)
    @GetMapping("/discipline")
    public ResponseEntity<List<UserSubDisciplineResponse>> getSkills() {
        User user = getAuthenticatedUser();
        List<UserSubDiscipline> userSubDisciplines = userSubDisciplineRepository.findByUserId(user.getId());  //find userSubdisciplines by users id

        if (userSubDisciplines.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        } //if user havent filled it yet, return emptyList

        List<UserSubDisciplineResponse> response = userSubDisciplines.stream()
                .sorted(Comparator.comparing(UserSubDiscipline::getSkillLevel).reversed())  //sort by skillLevel in descending order
                .map(userSubDiscipline -> new UserSubDisciplineResponse(
                        userSubDiscipline.getSubDiscipline().getId(),
                        userSubDiscipline.getSubDiscipline().getName(),
                        userSubDiscipline.getSubDiscipline().getCategory().getName(),
                        userSubDiscipline.getSkillLevel()
                ))
                .toList(); //created a list of blocks, where blocks filled with id and name of subdiscipline, and users skillLevel in it with category of that subdiscipline

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-disciplines")
    public ResponseEntity<List<SubDisciplineResponse>> getAllDisciplines() {
        User user = getAuthenticatedUser();
        List<SubDiscipline> subDisciplines = subDisciplineRepository.findAll();
        List<SubDisciplineResponse> response = subDisciplines.stream().map(subDiscipline ->
                new SubDisciplineResponse(subDiscipline.getId(), subDiscipline.getName())
        ).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    //endpoint for adding disciplines
    @PostMapping("/discipline/add")
    public ResponseEntity<String> addDisciplineToUser(@RequestBody Map<String, String> requestBody) {
        User user = getAuthenticatedUser();

        //if that subdiscipline doesnt exists return runtime exception
        SubDiscipline subDiscipline = subDisciplineRepository.findById(Long.parseLong(requestBody.get("subDisciplineId")))
                .orElseThrow(() -> new RuntimeException("Sub-discipline not found"));

        //checking for already having that discipline
        if (userSubDisciplineRepository.findByUserIdAndSubDisciplineId(user.getId(),
                Long.parseLong(requestBody.get("subDisciplineId"))).isPresent()) {
            return ResponseEntity.ok("You already have that discipline, it would be better if you will just edit it");
        }

        //getting skillLevel, and assuming that it is a number between 1 and 10
        int skillLevel = Integer.parseInt(requestBody.get("skillLevel"));
        if (skillLevel > 10 || skillLevel<1){
            return ResponseEntity.ok("You cannot your proficiency level with that, please use numbers between 1 and 10");
        }

        //saving user
        UserSubDiscipline userSubDiscipline = new UserSubDiscipline();
        userSubDiscipline.setUser(user);
        userSubDiscipline.setSubDiscipline(subDiscipline);
        userSubDiscipline.setSkillLevel(skillLevel);

        userSubDisciplineRepository.save(userSubDiscipline);
        return ResponseEntity.ok("Sub-Discipline added successfully");
    }

    @PutMapping("/discipline/edit")
    public ResponseEntity<String> editUserDiscipline(
            @RequestParam Long subDisciplineId, @RequestBody SkillUpdateRequest request) {
        User user = getAuthenticatedUser();

        // Find existing discipline
        UserSubDiscipline userSubDiscipline = userSubDisciplineRepository.findByUserIdAndSubDisciplineId(
                        user.getId(), subDisciplineId)
                .orElseThrow(() -> new RuntimeException("Sub-discipline not found for this user"));

        // Get level of skill
        int updatedSkillLevel = request.getSkillLevel();

        // Check for diapazon
        if (updatedSkillLevel < 1 || updatedSkillLevel > 10) {
            return new ResponseEntity<>("Skill level has to be between 1 and 10", HttpStatus.BAD_REQUEST);
        }

        // save change
        userSubDiscipline.setSkillLevel(updatedSkillLevel);
        userSubDisciplineRepository.save(userSubDiscipline);
        return new ResponseEntity<>("Способности пользователя обновлены успешно.", HttpStatus.OK);
    }

    //deleting mapping from user
    @DeleteMapping("/discipline/delete")
    public ResponseEntity<String> deleteUserDiscipline(@RequestParam Long subDisciplineId) {
        User user = getAuthenticatedUser();

        //finding
        List<UserSubDiscipline> userSubDiscipline = userSubDisciplineRepository.findAllByUserIdAndSubDisciplineId(user.getId(), subDisciplineId);

        //deleting
        userSubDisciplineRepository.deleteAll(userSubDiscipline);
        return ResponseEntity.ok("Sub-Discipline removed successfully");
    }
}
