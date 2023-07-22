package video;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FrameSequence {
    /**
     * Create FrameSequence file to list frames for FFMPEG concat operation
     * @param duration duration of future exported video
     * @param fps frames per second of exported video
     * @param grainFolder folder for grain frames
     * @param glitchFolder folder for glitch frames
     * @param impactFrames frame counts where glitch effects occur (1st frame)
     */
    public static void create(double duration, int fps, File grainFolder, File glitchFolder, ArrayList<Integer> impactFrames) {
        try {

            int frameCount = (int) Math.ceil(duration * fps);

            File frameListFile = new File("frameList.txt");
            frameListFile.createNewFile();

            // Delete File Contents before writing
            new FileWriter(frameListFile, false).close();

            FileWriter writer = new FileWriter(frameListFile,true);

            int grainCount = directorySize(grainFolder);
            int glitchCount = directorySize(glitchFolder);

            for (int i = 0; i < frameCount; i++) {
                writer.write("file '" + grainFolder + "\\" + (i % grainCount) + ".jpg'\n");
            }

            List<String> lines = Files.readAllLines(frameListFile.toPath());

            StringBuilder glitchFrames = new StringBuilder();

            for (int i = 0; i < glitchCount; i++) {
                glitchFrames.append("file '").append(glitchFolder).append("\\").append(i).append(".jpg'\n");
            }

            for (int i = 0; i < impactFrames.size(); i++) {

                lines.add(impactFrames.get(i) - (i * glitchCount), String.valueOf(glitchFrames));

                for (int j = 0; j < glitchCount; j++) {
                    lines.remove(lines.size() - 1);
                }
            }

            Files.write(frameListFile.toPath(), lines);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the count of files in a folder
     * @param folder folder location
     * @return count
     */
    private static int directorySize(File folder) {
        return Objects.requireNonNull(folder.listFiles()).length;
    }
}