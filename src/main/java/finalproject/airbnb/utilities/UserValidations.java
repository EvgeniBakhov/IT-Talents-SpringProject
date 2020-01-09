package finalproject.airbnb.utilities;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserValidations {

    public static final String EMAIL_REGEX = "^[A-z0-9-_.]+@[A-z0-9]+\\.[A-z]{2,6}$";
    public static final String NAME_REGEX = "^[A-z']{2,30}$";
    public static final String PHONE_NUMBER_REGEX = "^+[0-9]{1,4}[0-9]{9}$";
    public static final int MIN_AGE = 18;
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";


    public boolean isValidEmail(String email) {
        if(email != null && !email.isEmpty() && email.matches(EMAIL_REGEX)) {
            return true;
        }
        return false;
    }

    public boolean isValidName(String name) {
        if(name != null && !name.isEmpty() && name.matches(NAME_REGEX)) {
            return true;
        }
        return false;
    }

    public boolean isValidBirthday(LocalDate birthday) {
        Period period = Period.between(birthday, LocalDate.now());
        if(period.getYears() >= MIN_AGE) {
            return true;
        }
        return false;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if(phoneNumber != null && !phoneNumber.isEmpty() && phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            return true;
        }
        return false;
    }

    public boolean isValidLocation(String location) {
        if(location != null && !location.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isValidProfilePicture(String profilePicture){
        if(profilePicture != null && profilePicture.trim().isEmpty()){
            return true;
        }
        return false;
    }

    public boolean isValidPassword(String password) {
        if(password != null && password.isEmpty() && password.matches(PASSWORD_REGEX)){
            return true;
        }
        return false;
    }
}

