package website;

import java.util.List;

public record WebsiteRecord(
        String name,
        String date,
        String description,
        List<String> categoryNames,
        String mp3Id,
        String wavId,
        String stemId
) {}
