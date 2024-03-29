package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {

    private final HashService hashService;

    private final UserMapper userMapper;

    public UserService(HashService hashService, UserMapper userMapper) {
        this.hashService = hashService;
        this.userMapper = userMapper;
    }

    public int createUser(User user) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String hashedPassword = hashService.getHashedValue(user.getPassword(), encodedSalt);
        User newUser = new User
                (
                        null,
                        user.getUsername(),
                        encodedSalt,
                        hashedPassword,
                        user.getFirstName(),
                        user.getLastName()
                );

        return userMapper.insert(newUser);
    }

    public boolean isUsernameAvailable(String username) {
        return userMapper.getUser(username) == null;
    }


    public User getUser(String username) {
        return userMapper.getUser(username);
    }
}
