package joint;

import youtube.YoutubeRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class YoutubeJson {
    public static YoutubeRecord createYoutubeRecord(CsvRecord csvRecord, HashMap<String, Double> markers, int tempo) {
        String uniqueTags = Arrays.toString((csvRecord.tags()))
                .replace("[","")
                .replace("]","")
                .trim();

        String title = createTitle(csvRecord, uniqueTags);

        String tags = getTags(uniqueTags);

        String description = createDescription(csvRecord, markers, tempo);

        return new YoutubeRecord(csvRecord.localVideo(), csvRecord.localThumbnail(),
                title, tags, csvRecord.publishDateTime(), description);
    }

    private static String createTitle(CsvRecord record, String uniqueTags) {
        String title;

        if (record.trueType() == null) {
            title = (uniqueTags.replace(",","") + " Type Beat / " +
                    record.name() + " (Free For Profit)");
        } else {
            title = (record.trueType() + " Type Beat / " + record.name() +
                    " (Free For Profit)");
        }
        return title;
    }
    private static String getTags(String uniqueTags) {
        return "Free For Profit,H3 Music,Karaoke,Instrumental,Beat," +
                "Free,Download,Hip-Hop,Type Beat," + uniqueTags;
    }
    private static String createDescription(CsvRecord record, HashMap<String, Double> markers, int tempo) {

        return ("Hope you guys enjoy! Like Comment & Sub for more!\n\n" +
                "You can use on ALL platforms, just please tag when possible and keep" +
                " Content ID / Match Searching options off when uploading to distributors.\n" +
                "(Available under CC Attribution 4.0 International License)\n" +
                "⯈ Download & Purchase WAV + STEMS: https://www.h3music.com/beats\n" +
                "⯈ Email me for Custom Beat Inquires!\n\n" +
                "⯈ BPM " + tempo + ", " + record.tonality() + "\n" +
                "⯈ Breakdown (Don't have to follow this):\n" +
                printTimestamps(markers) + "\n\n" +
                "⯈ Socials:\n" +
                "⯈ Instagram: https://www.instagram.com/h3_music\n" +
                "⯈ Soundcloud: https://soundcloud.com/h3_music1\n" +
                "⯈ Youtube: https://www.youtube.com/c/h3music\n" +
                "⯈ Spotify: https://open.spotify.com/artist/1dm8Num9JhAelUhY7BV113?si=ymw0hmhvQyqUppnmFyzJ1A\n\n" +
                "⯈ Inquiries: info@h3music.org");
    }

    private static String printTimestamps(HashMap<String, Double> markers) {

        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();
        ArrayList<Double> list = new ArrayList<>();

        for (Map.Entry<String, Double> entry : markers.entrySet()) {
            list.add(entry.getValue());
        }

        list.sort(Double::compareTo);

        for (Double val : list) {
            int valInt = (int) round(val,2);

            for (Map.Entry<String, Double> entry : markers.entrySet()) {
                if (entry.getValue().equals(val)) {
                    sortedMap.put(entry.getKey(), String.format("%02d:%02d", valInt / 60, valInt % 60));
                }
            }
        }

        return sortedMap.toString()
                .replace(", ", "\n")
                .replace("=0", ": ")
                .replace("=", ": ")
                .replace("{", "")
                .replace("}", "");
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}