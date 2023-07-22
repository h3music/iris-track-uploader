package joint;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;

public record CsvRecord (
        LocalDateTime publishDateTime,
        String name,
        String slug,
        String[] tags,
        String tonality,
        String trueType,
        URI cloudRoot,
        File localMedia,
        File localArt,
        File localMp3,
        File localWav,
        File localStem,
        File localProductImage,
        File localBg,
        File localThumbnail,
        File localVideo
) {}
