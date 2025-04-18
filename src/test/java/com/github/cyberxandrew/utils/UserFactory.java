package com.github.cyberxandrew.utils;

import com.github.cyberxandrew.dto.user.UserCreateDTO;
import com.github.cyberxandrew.dto.user.UserDTO;
import com.github.cyberxandrew.dto.user.UserUpdateDTO;
import com.github.cyberxandrew.model.Role;
import com.github.cyberxandrew.model.User;
import org.openapitools.jackson.nullable.JsonNullable;

public class UserFactory {
    private static Long id = 1L;
    private static String login = "testLogin";
    private static String password = "testPassword";
    private static String fullName = "testFullName";

    public static User createUserToSave() {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFullName(fullName);
        return user;
    }

    public static UserDTO createUserDTO() {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(id);
        userDTO.setLogin(login);
        userDTO.setPassword(password);
        userDTO.setFullName(fullName);

        return userDTO;
    }

    public static UserCreateDTO createUserCreateDTO() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();

        userCreateDTO.setLogin(login);
        userCreateDTO.setPassword(password);
        userCreateDTO.setFullName(fullName);
        userCreateDTO.setRole(Role.CUSTOMER);

        return userCreateDTO;
    }

    public static UserUpdateDTO createUserUpdateDTO() {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();

        userUpdateDTO.setLogin(JsonNullable.of(login));
        userUpdateDTO.setPassword(JsonNullable.of(password));
        userUpdateDTO.setFullName(JsonNullable.of(fullName));

        return userUpdateDTO;
    }

    public static class UserBuilder {
        private Long id = 1L;
        private String login = "testDeparturePoint";
        private String password = "testDestinationPoint";
        private String fullName = "Name Surname MiddleName";

        public UserFactory.UserBuilder withUserId (Long id) {
            this.id = id;
            return this;
        }

        public UserFactory.UserBuilder withUserLogin (String login) {
            this.login = login;
            return this;
        }

        public UserFactory.UserBuilder withUserPassword(String password) {
            this.password = password;
            return this;
        }

        public UserFactory.UserBuilder withUserFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(id);
            user.setLogin(login);
            user.setPassword(password);
            user.setFullName(fullName);
            return user;
        }
    }
}

