package joint;

import drive.DriveRecord;
import website.WebsiteRecord;

import java.util.Arrays;

public class WebsiteJson {

    public static WebsiteRecord createWebsiteRecord(CsvRecord csvRecord, DriveRecord driveIdRecord, int tempo) {

        String name = csvRecord.name();

        String date = csvRecord.publishDateTime().toString().replace("15:00", "06:00:00Z");

        String description;
        if (csvRecord.trueType() == null) {
            description = "<b><i>" + name + "</i></b> is a "+
                    Arrays.toString(csvRecord.tags()).replace(",","")
                            .replace("[", "").replace("]","") +
                    " Type Beat.\nBPM " + tempo + ", " + csvRecord.tonality();
        } else {
            description = "<b><i>" + name + "</i></b> is a "+ csvRecord.trueType() +" Type Beat.\n" +
                    "BPM " + tempo + ", " + csvRecord.tonality();
        }

        return new WebsiteRecord(name, date, description, Arrays.asList(csvRecord.tags()),
                driveIdRecord.Mp3Id(), driveIdRecord.WavId(), driveIdRecord.StemId());
    }
}
