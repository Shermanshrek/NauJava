package ru.david.NauJava.services;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.david.NauJava.entity.User;
import ru.david.NauJava.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp(){
        testUser = new User("testUser", "password", "test", "test");
    }

    @Test
    void findByUsername_UserExists() {
        when(userRepository.findByUsername("testUser"))
                .thenReturn(Optional.of(testUser));
        Optional<User> result = userService.findByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void findByUsername_UserNotExists(){
        when(userRepository.findByUsername("nonexist"))
                .thenReturn(Optional.empty());
        Optional<User> result = userService.findByUsername("nonexist");
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexist");
    }

    @Test
    void addUser_SuccessfullyAdd(){
        when(userRepository.existsByUsername("testUser"))
                .thenReturn(Boolean.FALSE);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation ->
                invocation.getArgument(0));
        User result = userService.addUser(testUser);
        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().contains("USER"));
        verify(userRepository, times(1)).existsByUsername("testUser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void addUser_UsernameExists(){
        when(userRepository.existsByUsername("testUser")).thenReturn(Boolean.TRUE);

        EntityExistsException exist = assertThrows(EntityExistsException.class, () -> userService.addUser(testUser));
        assertEquals("Username already exists", exist.getMessage());
        verify(userRepository, times(1)).existsByUsername("testUser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createAdminUser_SuccessfullyCreated() {
        when(passwordEncoder.encode("admin123")).thenReturn("encodedAdminPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createAdminUser("admin", "admin123", "System", "Administrator");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("encodedAdminPassword", result.getPassword());
        assertTrue(result.getRoles().contains("ADMIN"));
        assertTrue(result.getRoles().contains("USER"));
        verify(userRepository, times(1)).save(any(User.class));
    }
}
