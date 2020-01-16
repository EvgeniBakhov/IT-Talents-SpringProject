package finalproject.airbnb.utilities;

import org.springframework.stereotype.Component;

@Component
public class PictureValidator {

    private static final String PICTURE_REGEX = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    public boolean isValidPicture(String fileName) {
        return fileName.matches(PICTURE_REGEX);
    }

}
