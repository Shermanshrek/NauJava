package ru.david.NauJava.services;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.david.NauJava.entity.User;
import ru.david.NauJava.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User addUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(user.getRoles().isEmpty()){
            user.addRole("USER");
        }

        return userRepository.save(user);
    }

    @Transactional
    public User createAdminUser(String username, String password,
                                String firstName, String lastName) {
        User admin = new User(username, password, firstName, lastName);
        admin.setPassword(passwordEncoder.encode(password));
        admin.addRole("ADMIN");
        admin.addRole("USER");
        return userRepository.save(admin);
    }
}
