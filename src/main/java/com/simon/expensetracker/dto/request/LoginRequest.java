package com.simon.expensetracker.dto.request;

import com.simon.expensetracker.Utilities.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Email is required")
    @ValidPassword(message = "Password too weak")
    private String password;
}
