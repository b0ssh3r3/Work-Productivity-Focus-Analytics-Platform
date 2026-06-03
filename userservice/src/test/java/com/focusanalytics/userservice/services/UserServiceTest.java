package com.focusanalytics.userservice.services;

import com.focusanalytics.userservice.UserRepository;
import com.focusanalytics.userservice.dto.RegisterRequest;
import com.focusanalytics.userservice.dto.UserResponse;
import com.focusanalytics.userservice.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerAccountReturnsExistingUserWhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setFirstName("Focus");
        request.setLastName("User");

        User existing = new User();
        existing.setId("user-1");
        existing.setEmail("user@example.com");
        existing.setFirstName("Focus");
        existing.setLastName("User");

        when(repository.existsByEmail("user@example.com")).thenReturn(true);
        when(repository.findByEmail("user@example.com")).thenReturn(existing);

        UserResponse response = userService.registerAccount(request);

        assertEquals("user-1", response.getId());
        assertEquals("user@example.com", response.getEmail());
        verify(repository, never()).save(any());
    }

    @Test
    void registerAccountPersistsNewUserWhenMissing() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setFirstName("New");
        request.setLastName("User");
        request.setPassword("secret");
        request.setKeycloakId("kc-1");

        User saved = new User();
        saved.setId("user-2");
        saved.setEmail("new@example.com");
        saved.setFirstName("New");
        saved.setLastName("User");
        saved.setKeycloakId("kc-1");

        when(repository.existsByEmail("new@example.com")).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.registerAccount(request);

        assertEquals("user-2", response.getId());
        assertEquals("kc-1", response.getKeycloakId());
        verify(repository).save(any(User.class));
    }

    @Test
    void existsByKeycloakIdDelegatesToRepository() {
        when(repository.existsByKeycloakId("kc-1")).thenReturn(true);

        assertTrue(userService.existsByKeycloakId("kc-1"));
    }

    @Test
    void getUserProfileThrowsWhenMissing() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserProfile("missing"));

        assertTrue(ex.getMessage().contains("Account profile not found"));
    }
}
