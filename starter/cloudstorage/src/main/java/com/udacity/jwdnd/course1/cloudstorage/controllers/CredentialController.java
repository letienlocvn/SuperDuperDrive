package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CredentialController {

    private CredentialService credentialService;

    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping("/credential")
    public String createOrUpdateCredential(Authentication authentication,
                                           Credential credential,
                                           RedirectAttributes redirectAttributes) {
        int rowEffected = -1;
        String message;
        if (credential.getCredentialId() == null) {
            rowEffected = credentialService.insertCredential(credential, authentication.getName());
            message = (rowEffected > 0) ? "Credential created successfully." : "Failed to create credential.";
        } else {
            rowEffected = credentialService.updateCredential(credential);
            message = (rowEffected > 0) ? "Credential updated successfully." : "Failed to update credential.";
        }
        redirectAttributes.addFlashAttribute((rowEffected > 0) ? "successMessage" : "errorMessage", message);

        return "redirect:/result";
    }

    @GetMapping("/credentials/delete/{credentialId}")
    public String deleteCredential(@PathVariable(name = "credentialId") Integer credentialId,
                                   RedirectAttributes redirectAttributes) {
        int rowEffected = credentialService.deleteCredential(credentialId);

        String message = (rowEffected > 0) ? "Credential deleted successfully." : "Failed to delete credential.";
        redirectAttributes.addFlashAttribute((rowEffected > 0) ? "successMessage" : "errorMessage", message);

        return "redirect:/result";
    }

}
