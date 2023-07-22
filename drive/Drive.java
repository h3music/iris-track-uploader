package drive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import joint.CsvRecord;

import java.io.IOException;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;

public class Drive {
    private static String ART_LOCATION = "";
    private static String MP3_LOCATION = "";
    private static String WAV_LOCATION = "";
    private static String STEM_LOCATION = "";

    /**
     * Method to upload files related to product through Drive API call
     * @param csvRecord Record from CSV file to get local file locations
     * @return DriveRecord of file Ids
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static DriveRecord upload(CsvRecord csvRecord) throws IOException, GeneralSecurityException {

        if (LocalDateTime.now().isAfter(LocalDateTime.of(2024,1,1,0,0))) {
            System.out.println("Need new Google Folders");
            System.exit(1);
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        com.google.api.services.drive.Drive service =
                new com.google.api.services.drive.Drive.Builder(
                Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.authorize(HTTP_TRANSPORT))
                .setApplicationName("drive")
                .build();

        String ArtId = request(service, csvRecord.localProductImage(), ART_LOCATION);
        System.out.println("Uploaded ART");
        String Mp3Id = request(service, csvRecord.localMp3(), MP3_LOCATION);
        System.out.println("Uploaded Mp3");
        String WavId = request(service, csvRecord.localWav(), WAV_LOCATION);
        System.out.println("Uploaded WAV");
        String StemId = request(service, csvRecord.localStem(), STEM_LOCATION);
        System.out.println("Uploaded STEM");

        return new DriveRecord(Mp3Id, WavId, StemId, ArtId);
    }

    /**
     * Method to request drive API to upload file
     * @param service drive api service
     * @param file File to upload to Drive
     * @param cloudFolder Folder in Google Drive to upload to
     * @return Google cloud drive ID of uploaded file
     */
    private static String request(com.google.api.services.drive.Drive service,
                                  java.io.File file, String cloudFolder) {
        File fileMetadata = new File()
                .setName(file.getName())
                .setParents(Collections.singletonList(cloudFolder));

        FileContent mediaContent = new FileContent("", file);

        try {
            File output = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            System.out.println("File ID: " + output.getId());

            return output.getId();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}