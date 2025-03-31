package com.github.cyberxandrew.repository;

import com.github.cyberxandrew.exception.user.UserDeletionException;
import com.github.cyberxandrew.exception.user.UserNotFoundException;
import com.github.cyberxandrew.exception.user.UserUpdateException;
import com.github.cyberxandrew.model.User;
import com.github.cyberxandrew.utils.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryImplTest {
    @Mock
    private Logger logger;
    @Mock private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private UserRepositoryImpl userRepository;

    private Long nonExistingId;
    private Long id1;
    private Long id2;
    private String login1;
    private String login2;
    private String password1;
    private String password2;
    private String fullName1;
    private String fullName2;
    private User testUser;

    @BeforeEach
    void beforeEach() {
        nonExistingId = 999L;
        id1 = 1L;
        id2 = 2L;
        login1 = "name1";
        login2 = "name2";
        password1 = "password1";
        password2 = "password2";
        fullName1 = "fullName1";
        fullName2 = "fullName2";
        
        testUser = new User();
        testUser.setId(id1);
    }

    @Test
    public void testFindByIdSuccessful() {
        String sql = "SELECT * FROM users WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{id1}), any(RowMapper.class)))
                .thenReturn(testUser);

        Optional<User> actual = userRepository.findById(id1);

        assertTrue(actual.isPresent());
        assertEquals(actual.get(), testUser);
    }

    @Test
    public void testFindByIdFailed() {
        String sql = "SELECT * FROM users WHERE id = ?";

        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{id1}), any(RowMapper.class)))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<User> actual = userRepository.findById(id1);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testFindAllSuccessful() {
        String sql = "SELECT * FROM users";

        List<User> users = new ArrayList<>();
        User user1 = UserFactory.createUserToSave();
        user1.setId(id1);
        User user2 = UserFactory.createUserToSave();
        user2.setId(id2);
        Collections.addAll(users, user1, user2);

        when(jdbcTemplate.query(eq(sql), any(RowMapper.class)))
                .thenReturn(users);

        List<User> allUsers = userRepository.findAll();
        assertFalse(allUsers.isEmpty());
        assertEquals(allUsers.size(), 2);
        assertTrue(allUsers.containsAll(Arrays.asList(user1, user2)));
    }

    @Test
    public void testSaveUser() {
        User userToSave = UserFactory.createUserToSave();

        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(new java.util.HashMap<String, Object>() {{ put("id", id1); }});
            return 1;});

        User savedUser = userRepository.save(userToSave);

        assertEquals(savedUser, userToSave);
        verify(jdbcTemplate, times(1)).update(any(PreparedStatementCreator.class),
                any(KeyHolder.class));
//        verify(logger, times(1)).debug(anyString(), anyLong());
    }

    @Test
    public void testUpdateUserSuccessful() {
        String sql = "UPDATE users SET login = ?, password = ?, full_name = ? WHERE id = ?";

        User userToUpdate = new User();
        userToUpdate.setId(id1);
        userToUpdate.setLogin(login1);
        userToUpdate.setPassword(password1);
        userToUpdate.setFullName(fullName1);

        when(jdbcTemplate.update(eq(sql), anyString(), anyString(), anyString(), anyLong())).thenReturn(1);

        User updatedUser = userRepository.update(userToUpdate);

        assertEquals(userToUpdate, updatedUser);
//        verify(logger, times(1)).debug(anyString(), anyLong());
    }

    @Test
    public void testUpdateUserFailed() {
        String sql = "UPDATE users SET login = ?, password = ?, full_name = ? WHERE id = ?";

        User userToUpdate = new User();
        userToUpdate.setId(id1);
        userToUpdate.setLogin(login1);
        userToUpdate.setPassword(password1);
        userToUpdate.setFullName(fullName1);

        when(jdbcTemplate.update(eq(sql), anyString(), anyString(), anyLong())).thenReturn(0);

        assertThrows(UserUpdateException.class, () -> userRepository.update(userToUpdate));
//        verify(logger, times(1)).warn(anyString(), anyLong());
    }

    @Test
    public void testDeleteByIdSuccessful() {
        User user = new UserFactory.UserBuilder()
                .withUserId(id1)
                .withUserLogin(login1)
                .withUserPassword(password1)
                .withUserFullName(fullName1)
                .build();
        String sql1 = "SELECT * FROM users WHERE id = ?";
        String sql2 = "DELETE FROM users WHERE id = ?";
        String sql3 = "SELECT COUNT(*) FROM users WHERE user_id = ?";

        when(jdbcTemplate.queryForObject(sql3, Integer.class, id1)).thenReturn(0);
        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{id1}), any(RowMapper.class)))
                .thenReturn(user);
        when(jdbcTemplate.update(eq(sql2), eq(id1))).thenReturn(1);

        userRepository.deleteById(id1);

        verify(jdbcTemplate, times(1)).update(eq(sql2), eq(id1));
//        verify(logger, times(1)).debug(anyString(), eq(userId));
    }

    @Test
    public void testDeleteByIdFailed() {
        String sql1 = "SELECT * FROM users WHERE id = ?";
        String sql2 = "DELETE FROM users WHERE id = ?";
        String sql3 = "SELECT COUNT(*) FROM users WHERE user_id = ?";

        when(jdbcTemplate.queryForObject(sql3, Integer.class, id1)).thenReturn(0);
        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{nonExistingId}), any(RowMapper.class)))
                .thenThrow(EmptyResultDataAccessException.class);

        assertThrows(UserNotFoundException.class, () -> userRepository.deleteById(nonExistingId));
        verify(jdbcTemplate, times(0)).update(eq(sql2), eq(nonExistingId));
//        verify(logger, times(1)).warn(anyString(), anyLong());
    }

    @Test
    public void testDeleteByIdDatabaseError() {
        User user = new User();
        String sql1 = "SELECT * FROM users WHERE id = ?";
        String sql2 = "DELETE FROM users WHERE id = ?";
        String sql3 = "SELECT COUNT(*) FROM users WHERE user_id = ?";

        when(jdbcTemplate.queryForObject(sql3, Integer.class, id1)).thenReturn(0);
        when(jdbcTemplate.queryForObject(eq(sql1), eq(new Object[]{id1}), any(RowMapper.class)))
                .thenReturn(user);
        when(jdbcTemplate.update(eq(sql2), eq(id1))).thenThrow(DataAccessResourceFailureException.class);

        assertThrows(UserDeletionException.class, () -> userRepository.deleteById(id1));
    }
}
