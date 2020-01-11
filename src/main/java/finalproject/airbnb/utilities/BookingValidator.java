package finalproject.airbnb.utilities;

import finalproject.airbnb.model.pojo.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingValidator {
    public boolean validateBooking(Booking booking){
        if(booking.getFromDate().compareTo(booking.getToDate())>=0){
            return false;
        }
        if(booking.getFromDate().compareTo(LocalDate.now())<0){
            return false;
        }
        return true;
    }
}
