package com.app.service.auth;

import com.app.model.user.User;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    /**
     * User repository.
     */
    private final UserRepository repository;

    /**
     * Сохранение пользователя.
     * @param user пользователь.
     * @return сохраненный пользователь
     */
    public User save(final User user) {
        return repository.save(user);
    }


    /**
     * Создание пользователя.
     * @param user пользователь.
     * @return созданный пользователь
     */
    public User create(final User user) {
        if (repository.existsByUsername(user.getUsername())) {
            // Заменить на свои исключения
            throw new RuntimeException(
                    "Пользователь с таким именем уже существует"
            );
        }

        return save(user);
    }

    /**
     * Получение пользователя по имени пользователя.
     * @param username username.
     * @return пользователь
     */
    public User getByUsername(final String username) {
        return repository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь не найден")
                );

    }

    /**
     * Получение пользователя по имени пользователя.
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя.
     *
     * @return текущий пользователь
     */
    public User getCurrentUser() {
        var username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return getByUsername(username);
    }
}
