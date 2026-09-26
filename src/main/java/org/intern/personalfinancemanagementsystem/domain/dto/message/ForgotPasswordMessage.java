package org.intern.personalfinancemanagementsystem.domain.dto.message;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordMessage {
    String email;
    String otp;
}
