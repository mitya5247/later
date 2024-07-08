package ru.practicum.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String registrationDate = String.valueOf(LocalDate.now());
    private UserState state = UserState.ACTIVE;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private String dateOfBirth = String.valueOf(LocalDate.of(1998, 12,12));
}
