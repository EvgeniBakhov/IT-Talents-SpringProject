package finalproject.airbnb.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import finalproject.airbnb.model.pojo.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String phoneNumber;
    private String userDescription;
    private String address;
    private String city;
    private String country;
    private String profilePicture;
}
