package com.example.URLShortner.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenRequest {

    @NotBlank(message = "URL is required")
    @URL(message = "Invalid URL formate")
    private String longUrl;

    @Pattern( regexp = "^[a-zA-Z0-9-]*$",
            message = "Alias can only contain letters, numbers, hyphens")
    @Size(min = 7, max = 30, message = "Expiry cannot exceed 365 days")
    private String customAlias;

    private Long ExpiryDate;


}
