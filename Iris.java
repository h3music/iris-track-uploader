import drive.Drive;
import drive.DriveRecord;
import joint.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import video.Video;
import video.VideoRecord;
import wav.WavFile;
import website.Website;
import website.WebsiteRecord;
import youtube.UploadVideo;
import youtube.YoutubeRecord;
import zip.Zip;

public class Iris {
    public static void main(String[] args) throws IOException {

        dev.timerStart();

        // Interface
        String input = "input.csv";
        CsvRecord csv = CsvReader.read(input);

        // Get WAV Metadata
        WavFile wav = new WavFile(csv.localWav().toURI());
        int tempo = (int) wav.getTempo();

        // Paralleled Processes
        ExecutorService executorService = Executors.newFixedThreadPool(9);

        Runnable taskA = () -> processAlpha(csv, tempo);
        Runnable taskB = () -> processBeta(csv, tempo, wav.getMarkers(), wav.getDuration());

        executorService.execute(taskA);
        executorService.execute(taskB);

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dev.timerEnd();
    }

    /**
     * Process A to be paralleled, process to create Zip, upload files to Google Drive, and create website product
     * @param csv csv file to get parameters
     * @param tempo the track tempo
     */
    private static void processAlpha(CsvRecord csv, int tempo) {
        try {
            // Create Zip
            Zip.create(csv.name(), csv.localMedia());

            // Drive & S3 Upload
            DriveRecord driveIdRecord = Drive.upload(csv);
            String s3Url = s3.s3Uploader.upload(csv.localMp3());

            // Website
            WebsiteRecord websiteRecord = WebsiteJson.createWebsiteRecord(csv, driveIdRecord, tempo);
            Website.upload(websiteRecord, s3Url);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("---Alpha Completed!");
    }

    /**
     * Process B to be paralleled, process to create Video, and upload it to Youtube
     * @param csv csv file to get parameters
     * @param tempo the track tempo
     * @param markers markers in audio file
     * @param duration duration of audio file
     */
    private static void processBeta(CsvRecord csv, int tempo, HashMap<String, Double> markers, double duration) {

        // Video Rendering
        VideoRecord videoRecord = VideoJson.createVideoRecord(csv, markers);

        File grainResource = new File("resources/grain/");

        Video.render(duration, videoRecord.impactFrames(),
                videoRecord.sourceFile(), grainResource, videoRecord.audio(), videoRecord.outputFile());

        // YouTube Uploading
        YoutubeRecord youtubeRecord = YoutubeJson.createYoutubeRecord(csv, markers, tempo);
        UploadVideo.upload(youtubeRecord);

        System.out.println("---Beta Completed!");
    }
}
