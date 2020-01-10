package finalproject.airbnb.model.pojo;

import finalproject.airbnb.model.dto.RegisterUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String phoneNumber;
    private LocalDate joinDate;
    private String userDescription;
    private Location location;
    private String profilePicture;

    public User(RegisterUserDTO registerUserDTO) {
        this(0,
                registerUserDTO.getEmail(),
                registerUserDTO.getPassword(),
                registerUserDTO.getFirstName(),
                registerUserDTO.getLastName(),
                registerUserDTO.getBirthday().toInstant().atZone(ZoneId.of("Europe/Sofia")).toLocalDate(),
                registerUserDTO.getPhoneNumber(),
                LocalDate.now(),
                registerUserDTO.getUserDescription(),
                new Location(registerUserDTO.getAddress(),registerUserDTO.getCity(), registerUserDTO.getCountry()),
                registerUserDTO.getProfilePicture());
    }

}
