package video;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class SeqToVideo {
    /**
     * Creates and sends command to FFMPEG to build the video.
     */
    public static void export(File outputFile, int fps, File audio) {
        try {

            // Change depending on where FFMPEG is installed
            String ffmpegPath = System.getProperty("user.home") +
                    "/repos/sources/cli/ffmpeg/bin/ffmpeg";

            String frameList = new File("frameList.txt").getAbsolutePath();
            String outputPath = outputFile.getAbsolutePath();

//            String[] command = new String[]{
//                    ffmpegPath, "-y", "-r", String.valueOf(fps), "-f", "concat", "-safe",
//                    "0", "-i", frameList, "-crf", "30", outputPath
//            };

            String[] command = new String[]{
                    ffmpegPath, "-y", "-r", String.valueOf(fps), "-f", "concat", "-safe",
                    "0", "-i", frameList, "-i", String.valueOf(audio), "-crf", "30", outputPath
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            flushInputStreamReader(process);

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Video export successful!");
            } else {
                System.out.println("Video export failed.");
            }
        } catch (IOException|InterruptedException e) {
            System.out.println("An error occurred in the FFMPEG process" +
                    "creation (video.SeqToVideo.java)");
            e.printStackTrace();
        }
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
