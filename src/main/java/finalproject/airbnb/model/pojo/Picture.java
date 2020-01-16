package finalproject.airbnb.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Picture {
    private long id;
    private long stayId;
    private String pictureUrl;
}
