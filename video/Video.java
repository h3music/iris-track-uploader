package video;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Video {
    /**
     * Main render method for exporting a video using FFMPEG
     * @param duration duration of future exported video
     * @param fps frames per second of exported video
     * @param impactFrames frame counts where glitch effects occur (1st frame)
     * @param maxThreads number of frames to use for frame creating
     * @param sourceFile image file to build video based on
     * @param grainResource folder for grain resources
     * @param grainOutput folder for grain frames
     * @param glitchOutput folder for glitch frames
     * @param audio audio file to use in video
     * @param outputFile location to export video to
     */
    public static void render(double duration, int fps, ArrayList<Integer> impactFrames,
                              int maxThreads, File sourceFile, File grainResource, File grainOutput,
                              File glitchOutput, File audio, File outputFile) {

        ArrayList<File> grainResourceFiles = new ArrayList<>(
                Arrays.asList(Objects.requireNonNull(grainResource.listFiles())));

        GrainEffect.grain(sourceFile,grainResourceFiles, grainOutput,.3,96, maxThreads);

        ArrayList<File> grainFiles = new ArrayList<>(
                Arrays.asList(Objects.requireNonNull(grainOutput.listFiles())));

        GlitchEffect.glitch(grainFiles, glitchOutput, 5, maxThreads, 48);

        FrameSequence.create(duration, fps, grainOutput, glitchOutput, impactFrames);

        SeqToVideo.export(outputFile, fps, audio);
    }

    /**
     * Overload render method to auto set fps to 24, and maxThreads to 8
     * @param duration duration of future exported video
     * @param impactFrames frame counts where glitch effects occur (1st frame)
     * @param sourceFile image file to build video based on
     * @param grainResource folder for grain resources
     * @param grainOutput folder for grain frames
     * @param glitchOutput folder for glitch frames
     * @param audio audio file to use in video
     * @param outputFile location to export video to
     */
    public static void render(double duration,  ArrayList<Integer> impactFrames,  File sourceFile,
                              File grainResource, File grainOutput, File glitchOutput, File audio, File outputFile) {

        render(duration, 24, impactFrames, 8, sourceFile, grainResource,
                grainOutput, glitchOutput, audio, outputFile);
    }

    /**
     * Overloaded method to auto set fps to 24, maxThreads to 8, and create temp folders for grain and glitch frames
     * @param duration duration of future exported video
     * @param impactFrames frame counts where glitch effects occur (1st frame)
     * @param sourceFile image file to build video based on
     * @param grainResource folder for grain resources
     * @param audio audio file to use in video
     * @param outputFile location to export video to
     */
    public static void render(double duration,  ArrayList<Integer> impactFrames, File sourceFile,
                              File grainResource, File audio, File outputFile) {

        File tmpFolder = new File("./tmp");
        File grainOutput = new File("./tmp/grain");
        File glitchOutput = new File("./tmp/glitch");

        if (tmpFolder.exists()) {
            tmpFolder.delete();
        }

        tmpFolder.mkdir();
        grainOutput.mkdir();
        glitchOutput.mkdir();

        render(duration, 24, impactFrames, 8, sourceFile, grainResource, grainOutput, glitchOutput, audio, outputFile);
    }
}
