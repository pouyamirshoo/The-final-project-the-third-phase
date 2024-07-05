package com.example.finalprojectthirdphase.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentModel {

    @NotBlank
    @Pattern(regexp = "[0-9]+")
    @Size(max = 16, min = 16)
    private String cardNumber;
    @NotBlank
    @Pattern(regexp = "[0-9]+")
    @Size(max = 5, min = 3)
    private String cvv2;
    @NotNull
    @Range(min = 1, max = 12)
    private Integer month;
    @NotNull
    @Range(min = 1403, max = 1410)
    private Integer year;
    @NotBlank
    @Pattern(regexp = "[0-9]+")
    @Size(max = 8, min = 5)
    private String password;
}

