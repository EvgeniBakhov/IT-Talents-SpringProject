package finalproject.airbnb.utilities;

import finalproject.airbnb.model.pojo.Location;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class UserValidator {

    public static final String EMAIL_REGEX = "^[A-z0-9-_.]+@[A-z0-9]+\\.[A-z]{2,6}$";
    public static final String NAME_REGEX = "^[A-z']{2,30}$";
    public static final String PHONE_NUMBER_REGEX = "^+[0-9]{1,4}[0-9]{9}$";
    public static final int MIN_AGE = 18;
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    private static final int MAX_AGE = 120;


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
        if(period.getYears() >= MIN_AGE && period.getYears()<= MAX_AGE) {
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

    public boolean isValidLocation(Location location) {
       if(!location.getAddress().matches("[A-z0-9-. ]{10,100}$")){
        return false;
       }
       if(!location.getCity().matches("[A-z- ]{3,30}$")){
        return false;
       }
       if(!location.getCountry().matches("[A-z- ]{3,20}")){
        return false;
       }
       return true;
    }


    public boolean isValidPassword(String password) {
        if(password != null && password.isEmpty() && password.matches(PASSWORD_REGEX)){
            return true;
        }
        return false;
    }
}

