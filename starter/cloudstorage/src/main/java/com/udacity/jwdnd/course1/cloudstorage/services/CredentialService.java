package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    private UserMapper userMapper;
    private CredentialMapper credentialMapper;
    private EncryptionService encryptionService;

    public CredentialService(UserMapper userMapper, CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.userMapper = userMapper;
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public List<Credential> getAllCredentials(String username) {
        List<Credential> credentialList = credentialMapper.getAllCredentials(userMapper.getUser(username).getUserId());
        if (credentialList == null) {
            credentialList = new ArrayList<>();
        }
        return credentialList.stream().map(this::decryptPassword).collect(Collectors.toList());
    }

    public Credential decryptPassword(Credential credential) {
        credential.setPassword(encryptionService.decryptValue(credential.getPassword(), credential.getKey()));
        return credential;
    }

    public int insertCredential(Credential credential, String username) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptPassword = encryptionService.encryptValue(credential.getPassword(), encodedKey);

        Integer userId = userMapper.getUser(username).getUserId();
        Credential newCredential = new Credential();
        newCredential.setUrl(credential.getUrl());
        newCredential.setUsername(credential.getUsername());

        newCredential.setPassword(encryptPassword);
        newCredential.setKey(encodedKey);

        newCredential.setUserId(userId);

        return credentialMapper.insertCredential(newCredential);
    }

    public int updateCredential(Credential credential) {
        Credential updateCredential = credentialMapper.findCredentialById(credential.getCredentialId());

        credential.setKey(updateCredential.getKey());
        String encryptPassword = encryptionService.encryptValue(credential.getPassword(), credential.getKey());
        credential.setPassword(encryptPassword);
        return credentialMapper.updateCredential(credential);
    }

    public int deleteCredential(Integer credentialId) {
        return credentialMapper.deleteCredential(credentialId);
    }
}
