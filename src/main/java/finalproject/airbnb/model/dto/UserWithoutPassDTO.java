package finalproject.airbnb.model.dto;

import finalproject.airbnb.model.pojo.Location;
import finalproject.airbnb.model.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWithoutPassDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String phoneNumber;
    private LocalDate joinDate;
    private String userDescription;
    private Location location;
    private String profilePicture;

    public UserWithoutPassDTO(User user) {
        this(user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthday(),
                user.getPhoneNumber(),
                user.getJoinDate(),
                user.getUserDescription(),
                user.getLocation(),
                user.getProfilePicture());
    }

}
