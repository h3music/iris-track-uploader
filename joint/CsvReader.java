package joint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvReader {
    /**
     * Method to read CSV file to get product and advertisement parameters
     * @param file CSV File to read
     * @return CsvRecord
     */
    public static CsvRecord read(String file) {

        List<List<String>> rows = readLines(file);

        String name = rows.get(1).get(1);
        String slug = name.replace("[^a-zA-Z0-9]", "").toLowerCase();

        String trueType;

        try {
            trueType = rows.get(2).get(1)
                    .replace("\"\"\"", "\"")
                    .replace("\"\"", "\"")
                    .substring(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            trueType = null;
        }


        String[] tags = new String[rows.get(3).size()-1];
        for (int i = 1; i < rows.get(3).size(); i++) {
            tags[i-1] = rows.get(3).get(i).replace("\"", "");
        }

        String tonality = rows.get(4).get(1);
        LocalDateTime date = convertToLocalDateTime(rows.get(5).get(1));
        URI cloudRoot = URI.create(rows.get(6).get(1));

        File localMedia = new File(rows.get(7).get(1));
        File localArt = new File(rows.get(8).get(1));
        File localMp3 = new File(localMedia.getAbsolutePath() + "\\H3Music_" + name + ".mp3");
        File localWav = new File(localMedia.getAbsolutePath() + "\\H3Music_" + name + "_Master.wav");
        File localStem = new File(localMedia.getAbsolutePath() + "\\H3Music_" + name + ".zip");
        File localProductImage = new File(localArt.getAbsolutePath() + "\\" + slug + ".jpg");
        File localBg = new File(localArt.getAbsolutePath() + "\\Analog Efex Pro 2.jpg");
        File localThumbnail = new File(localArt.getAbsolutePath() + "\\thumb.jpg");
        File localVideo = new File(localArt + "\\comp.mp4");

        CsvRecord csvRecord = new CsvRecord(date, name, slug, tags, tonality,
                trueType, cloudRoot, localMedia, localArt, localMp3, localWav,
                localStem, localProductImage, localBg, localThumbnail, localVideo);

        boolean validated = validateJsonData(csvRecord);

        if(!validated) {
            System.out.println("Json Data is not Validated");
            System.exit(1);
        }

        System.out.println("Data is Validated");

        return csvRecord;
    }

    /** Method to create list of read CSV data
     * @param file CSV File to read
     * @return list of read CSV data
     */
    private static List<List<String>> readLines(String file) {
        String line;
        List<List<String>> lines = new ArrayList<>();
        try (BufferedReader br =
                     new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                List<String> values = Arrays.asList(line.split(","));
                lines.add(values);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * convert String date to localDateTime
     * @param date String version of data (format: M/d/yy)
     * @return LocalDateTime
     */
    private static LocalDateTime convertToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");

        return LocalDate.parse(date, formatter).atStartOfDay().plusHours(15);
    }

    /**
     * Method validates date is in the future, and files in CSV exist
     * @param csvRecord CSVRecord
     * @return boolean if CSV Data is validated
     */
    private static boolean validateJsonData(CsvRecord csvRecord) {

        if(LocalDateTime.now().isAfter(csvRecord.publishDateTime().minusHours(6))) {
            System.out.println("Publish Date and Time is in the Past, please update" +
                    "to future Date and Time.");
            return false;
        }

        if(!csvRecord.localMedia().exists() ||
                !csvRecord.localArt().exists() ||
                !csvRecord.localMp3().exists() ||
                !csvRecord.localWav().exists() ||
                !csvRecord.localProductImage().exists() ||
                !csvRecord.localBg().exists() ||
                !csvRecord.localThumbnail().exists()) {
            System.out.println("A file in the Media or Art Directory is not named " +
                    "properly or does not exist.");
            return false;
        }

        return true;
    }
}
