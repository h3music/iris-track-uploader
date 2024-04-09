package s3;

import java.io.*;

public class s3Uploader {
    public static String upload(File uploadFile) {
        try {

            // Change depending on where script is installed
            String scriptPath = "";

            String uploadFileAbsolutePath = uploadFile.getAbsolutePath();

            String[] command = new String[]{
                    "node", scriptPath, uploadFileAbsolutePath
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Get the input stream of the process
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String uploadUrl = bufferedReader.readLine();

            flushInputStreamReader(process);

            int exitCode = process.waitFor();

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

            if (exitCode == 0) {
                System.out.println("S3 Upload successful!");
                System.out.println(uploadUrl);
                return uploadUrl;
            } else {
                System.out.println("S3 Upload failed.");
                return "";
            }
        } catch (IOException|InterruptedException e) {
            System.out.println("An error occurred in the s3 upload process" +
                    "creation (s3.s3Uploader.java)");
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Clears Input Stream so FFMPEG doesn't obstruct Java
     * @param process
     */
    private static void flushInputStreamReader (Process process) {
        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder s = new StringBuilder();
            while ((line = input.readLine()) != null) {
                s.append(line);
            }
        } catch (IOException e) {
            System.out.println("An error occurred in the flushing of FFMPEG" +
                    "(video.SeqToVideo.java)");
            e.printStackTrace();
        }
    }
}
