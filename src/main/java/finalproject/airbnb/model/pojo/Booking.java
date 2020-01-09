package finalproject.airbnb.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

}
