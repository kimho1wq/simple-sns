package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.fixture.UserEntityFixture;
import com.example.sns.model.entity.UserEntity;
import com.example.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import java.util.Optional;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        String username = "username";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(username, password, 1L);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(username, password, 1L));
        Assertions.assertDoesNotThrow(() -> userService.join(username, password));
    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
        String username = "username";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(username, password, 1L);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));
        SnsApplicationException e = Assertions.assertThrows(
                SnsApplicationException.class, () -> userService.join(username, password));
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        String username = "username";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(username, password, 1L);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(fixture));
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> userService.login(username, password));
    }

    @Test
    void 로그인시_userName으로_회원가입한_유저가_없는경우() {
        String username = "username";
        String password = "password";

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.empty());
        SnsApplicationException e = Assertions.assertThrows(
                SnsApplicationException.class, () -> userService.login(username, password));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());

    }

    @Test
    void 로그인시_패스워드가_틀린_경우() {
        String username = "username";
        String password = "password";
        String wrongPassword = "wrongPassword";
        UserEntity fixture = UserEntityFixture.get(username, password, 1L);

        when(userEntityRepository.findByUsername(username)).thenReturn(Optional.of(fixture));
        SnsApplicationException e = Assertions.assertThrows(
                SnsApplicationException.class, () -> userService.login(username, wrongPassword));
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());

    }


}
