package finalproject.airbnb.model.pojo;

import finalproject.airbnb.model.dto.BookingDTO;
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
public class Booking {

    private long id;
    private long stayId;
    private long userId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private boolean isAccepted;
    private boolean isValid;

    public Booking(BookingDTO bookingDTO, long userId, long stayId) {
        this.stayId = stayId;
        this.userId = userId;
        this.fromDate = bookingDTO.getFromDate().toInstant().atZone(ZoneId.of("Europe/Sofia")).toLocalDate();
        this.toDate = bookingDTO.getToDate().toInstant().atZone(ZoneId.of("Europe/Sofia")).toLocalDate();
        this.isValid = true;
    }
}
