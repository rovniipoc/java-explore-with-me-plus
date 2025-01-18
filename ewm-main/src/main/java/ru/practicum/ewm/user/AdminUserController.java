package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsersByParams(@RequestParam(required = false) List<Long> ids,
                                          @RequestParam(required = false, defaultValue = "0") Integer from,
                                          @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Поступил запрос Get /admin/users на получение List<UserDto> с параметрами ids = {}, from = {}, size = {}", ids, from, size);
        List<UserDto> response = userService.getUsersByParams(ids, from, size);
        log.info("Сформирован ответ Get /admin/users с телом: {}", response);
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createNewUser(@Validated @RequestBody NewUserRequest newUserRequest) {
        log.info("Поступил запрос Post /admin/users на создание User с телом {}", newUserRequest);
        UserDto response = userService.createNewUser(newUserRequest);
        log.info("Сформирован ответ Post /admin/users с телом: {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        log.info("Поступил запрос Delete /admin/users/{} на удаление User с id = {}", id, id);
        userService.deleteUserById(id);
        log.info("Выполнен запрос Delete /admin/users/{} на удаление User с id = {}", id, id);
    }
}
