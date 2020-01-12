package finalproject.airbnb.utilities;

import finalproject.airbnb.model.pojo.Location;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserValidator {

    private static final String EMAIL_REGEX = "^[A-z0-9-_.]+@[A-z0-9]+\\.[A-z]{2,6}$";
    private static final String NAME_REGEX = "^[A-z']{2,30}$";
    private static final String PHONE_NUMBER_REGEX = "^\\+[0-9]{11,15}$";
    private static final int MIN_AGE = 18;
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    private static final int MAX_AGE = 120;


    public boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && email.matches(EMAIL_REGEX);
    }

    public boolean isValidName(String name) {
        return name != null && !name.isEmpty() && name.matches(NAME_REGEX);
    }

    public boolean isValidBirthday(LocalDate birthday) {
        Period period = Period.between(birthday, LocalDate.now());
        return period.getYears() >= MIN_AGE && period.getYears() <= MAX_AGE;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && !phoneNumber.isEmpty() && phoneNumber.matches(PHONE_NUMBER_REGEX);
    }

    public boolean isValidPassword(String password) {
        return password != null && !password.isEmpty() && password.matches(PASSWORD_REGEX);
    }
}

