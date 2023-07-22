package youtube;

import java.io.File;
import java.time.LocalDateTime;

public record YoutubeRecord (
        File video,
        File thumbnail,
        String title,
        String tags,
        LocalDateTime publishDateTime,
        String description
){}
