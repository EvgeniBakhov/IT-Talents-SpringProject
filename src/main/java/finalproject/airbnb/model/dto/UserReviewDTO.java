package finalproject.airbnb.model.dto;

import finalproject.airbnb.model.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String profilePicture;

    public UserReviewDTO(User user) {
        setId(user.getId());
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setProfilePicture(user.getProfilePicture());
    }
}
