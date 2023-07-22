package joint;

import video.VideoRecord;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoJson {
    public static VideoRecord createVideoRecord(CsvRecord csvRecord, HashMap<String, Double> markers) {
        String[] mainMarkers = {"Chorus", "Chorus 1", "Chorus 2", "Chorus 3",
                "Chorus 4", "Chorus 5", "Drop", "Drop 1", "Drop 2", "Drop 3",
                "Drop 4", "Drop 5", "Hook", "Hook 1", "Hook 2", "Hook 3",
                "Hook 4", "Hook 5"};
        String[] altMarkers = {"Verse", "Verse 1", "Verse 2", "Verse 3",
                "Verse 4", "Verse 5"};

        ArrayList<Integer> impactMarkers = new ArrayList<>();

        for (String marker : mainMarkers) {
            if (markers.containsKey(marker)) {
                double value = markers.get(marker);
                impactMarkers.add((int) Math.ceil(24.0 * value));
            }
        }

        if (impactMarkers.isEmpty()) {
            for (String marker : altMarkers) {
                if (markers.containsKey(marker)) {
                    double value = markers.get(marker);
                    impactMarkers.add((int) Math.ceil(24.0 * value));
                }
            }
        }

        if (impactMarkers.isEmpty()) {
            System.out.println("There are no standard Timestamps. Please Fix.");
            System.exit(1);
        }

        return new VideoRecord(impactMarkers, csvRecord.localBg(),
                csvRecord.localWav(), csvRecord.localVideo());
    }

}
