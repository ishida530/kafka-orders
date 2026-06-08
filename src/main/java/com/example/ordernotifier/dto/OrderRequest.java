package com.example.ordernotifier.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class OrderRequest {

    @NotBlank(message = "trackingNumber is required")
    private String trackingNumber;

    @NotBlank
    @Email(message = "recipientEmail must be valid")
    private String recipientEmail;

    @NotBlank
    @Size(min = 2, max = 3)
    private String recipientCountryCode;

    @NotBlank
    @Size(min = 2, max = 3)
    private String senderCountryCode;

    @NotNull
    @Min(0) @Max(100)
    private Integer statusCode;
}