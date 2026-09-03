package org.intern.personalfinancemanagementsystem.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import org.intern.personalfinancemanagementsystem.constant.CommonConstant;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false)
    UUID id;

    @Column(name = "full_name", length = CommonConstant.FULL_NAME_MAX_LENGTH)
    String fullName;

    @Column(nullable = false, unique = true, length = CommonConstant.EMAIL_MAX_LENGTH)
    String email;

    @Column(nullable = false, length = CommonConstant.PASSWORD_MAX_LENGTH)
    String password;

    @Column(name = "phone_number", length = CommonConstant.PHONE_NUMBER_MAX_LENGTH)
    String phoneNumber;

    @Column(name = "date_of_birth", length = CommonConstant.DATE_OF_BIRTH_LENGTH)
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role = Role.USER;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;

    @Column(length = CommonConstant.User.AVATAR_LENGTH)
    String avatar;

    @Column(name = "password_changed_at")
    Instant passwordChangedAt;
}
