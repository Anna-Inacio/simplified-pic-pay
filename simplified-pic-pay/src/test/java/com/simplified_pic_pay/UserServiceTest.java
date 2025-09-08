package com.simplified_pic_pay;

import com.simplified_pic_pay.domain.user.User;
import com.simplified_pic_pay.domain.user.UserType;
import com.simplified_pic_pay.dtos.UserDTO;
import com.simplified_pic_pay.repository.UserRepository;
import com.simplified_pic_pay.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository repository;

    @Test
    void shouldCreateANewUser() {
        var newUser = createUserDTO(BigDecimal.valueOf(100.00), UserType.COMMON);

        when(repository.findUserByDocument(any(String.class))).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenReturn(new User(newUser));

        userService.createUser(newUser);

        assertNotNull(newUser);
        assertEquals("Maria", newUser.firstName());
        assertEquals("111.111.111-11", newUser.document());
        assertEquals(UserType.COMMON, newUser.userType());
    }

    @Test
    void shouldReturnExceptionWhenADocumentAlreadyExist() {
        var newUser = createUserDTO(BigDecimal.valueOf(100.00), UserType.COMMON);

        when(repository.findUserByDocument(any(String.class))).thenReturn(Optional.of(new User(newUser)));

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(newUser));
    }

    private static UserDTO createUserDTO(BigDecimal balance, UserType userType) {
        var newUser = new UserDTO(
                "Maria",
                "Oliveira",
                "111.111.111-11",
                "maria@gmail.com",
                "123456",
                balance,
                userType);
        return newUser;
    }
}
