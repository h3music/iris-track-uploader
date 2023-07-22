package video;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * This class creates composite image frames of an offset glitch effect applied to a still image or frames of a video
 */
public class GlitchEffect {
    /**
     * This is the master Glitch Effect method. It provides a foundation for overload methods.
     * @param source source An array list of image files, usually video frames, to have the glitch effect applied to.
     * @param outputFolder An output folder location for the composite images.
     * @param amplification How strong the effect is (Usually a value between 1-5 suffices)
     * @param maxThreads Hard limits the amount of threads used by the method.
     *      If heap memory errors are occurring lower this number.
     *      Performance has diminishing returns when maxThreads > physical CPU cores.
     *      Default value in overloaded methods is 4.
     * @param maxLength The max number of frames to render. Only if the source input is larger.
     *                  Ex. Applicable when you don't want to render 128 glitch frames from the source if you only need 48.
     */
    public static void glitch(ArrayList<File> source, File outputFolder, int amplification, int maxThreads, int maxLength) {

        ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);

        int length = source.size();

        if (source.size() > maxLength) {
            length = maxLength;
        }

        for (int i = 0; i < length; i++) {

            int sourceI = ((i % length) + length) % length;
            int finalI = i;

            File sourceFile = source.get(sourceI);

            Runnable task = () -> glitchProcessing(sourceFile, finalI, outputFolder, amplification, maxLength);

            executorService.execute(task);
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is an overloaded method to add grain to an array of source images,
     * when max threads is not specified (defaulted to 4).
     * @param source source An array list of image files, usually video frames, to have the glitch effect applied to.
     * @param outputFolder An output folder location for the composite images.
     * @param amplification How strong the effect is (Usually a value between 1-5 suffices)
     * @param maxLength The max number of frames to render. Only if the source input is larger.
     *                  Ex. Applicable when you don't want to render 128 glitch frames from the source if you only need 48.
     */
    public static void glitch(ArrayList<File> source, File outputFolder, int amplification, int maxLength) {
        glitch(source, outputFolder, amplification, 4, maxLength);
    }

    /**
     * This is an overloaded method to add grain to a single source image.
     * @param source source An array list of image files, usually video frames, to have the glitch effect applied to.
     * @param outputFolder An output folder location for the composite images.
     * @param amplification How strong the effect is (Usually a value between 1-5 suffices)
     * @param maxThreads Hard limits the amount of threads used by the method.
     *      If heap memory errors are occurring lower this number.
     *      Performance has diminishing returns when maxThreads > physical CPU cores.
     *      Default value in overloaded methods is 4.
     * @param maxLength The max number of frames to render. Only if the source input is larger.
     *                  Ex. Applicable when you don't want to render 128 glitch frames from the source if you only need 48.
     */
    public static void glitch(File source, File outputFolder, int amplification, int maxThreads, int maxLength) {

        ArrayList<File> sourceArray = new ArrayList<>();
        sourceArray.add(source);

        glitch (sourceArray, outputFolder, amplification, maxThreads, maxLength);
    }

    /**
     * This is an overloaded method to add grain to a single source image,
     * when max threads is not specified (defaulted to 4).
     * @param source source An array list of image files, usually video frames, to have the glitch effect applied to.
     * @param outputFolder An output folder location for the composite images.
     * @param amplification How strong the effect is (Usually a value between 1-5 suffices)
     * @param maxLength The max number of frames to render. Only if the source input is larger.
     *                  Ex. Applicable when you don't want to render 128 glitch frames from the source if you only need 48.
     */
    public static void glitch(File source, File outputFolder, int amplification,  int maxLength) {
        glitch(source, outputFolder, amplification, 4, maxLength);
    }

    /**
     * This method provides the logic for individual composite image creation.
     * @param sourceFile An image file to have the grain overlay applied to.
     * @param i The iteration to name the output composite file.
     * @param outputFolder An output folder location for the composite images.
     * @param amplification How strong the effect is (Usually a value between 1-5 suffices)
     * @param maxLength The max number of frames to render. Only if the source input is larger.
     *                  Ex. Applicable when you don't want to render 128 glitch frames from the source if you only need 48.
     */
    private static void glitchProcessing(File sourceFile, int i, File outputFolder, int amplification, int maxLength) {

        double inside = (Math.log(0.01) / (maxLength)) * i;
        double multiplier = amplification * Math.pow(Math.E, inside);

        int dX = randomOffsetAmount(200, multiplier);
        int dY = randomOffsetAmount(200, multiplier);
        int dRX = randomOffsetAmount(100, multiplier);
        int dRY = randomOffsetAmount(100, multiplier);
        int dGX = randomOffsetAmount(100, multiplier);
        int dGY = randomOffsetAmount(100, multiplier);
        int dBX = randomOffsetAmount(100, multiplier);
        int dBY = randomOffsetAmount(100, multiplier);

        try {
            BufferedImage source = ImageIO.read(sourceFile);
            File outputFile = new File(outputFolder, i + ".jpg");

            BufferedImage output = RgbOffset.offset(source, dX, dY, dRX, dRY, dGX, dGY, dBX, dBY);

            ImageIO.write(output, "jpg", outputFile);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Method to create a random int within range and multiplier parameters
     * @param range The range size for the random integer
     * @param multiplier The amplification of the randomized integer
     * @return An integer formed from the random value within the range, multiplied by the multiplier
     */
    private static int randomOffsetAmount(int range, double multiplier) {

        int x = range / -2;
        int y = range / 2 + 1;

        return (int) (ThreadLocalRandom.current().nextInt(x, y) * multiplier);
    }
}
