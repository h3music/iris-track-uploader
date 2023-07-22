package video;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class to Offset the position of an inputted BufferedImage. Functionality allows offset of individual channels as well.
 */
public class RgbOffset {
    /**
     * Method to apply the position offset. This method only provides pixel offsets, not individual color channels
     * @param image the BufferedImage inputted to have the offset applied to
     * @param xOffset the amount of pixels position should offset by in the X (Width) direction
     * @param yOffset the amount of pixels position should offset by in the Y (Height) direction
     * @return the BufferedImage with the position offsets applied
     */
    public static BufferedImage offset(BufferedImage image, int xOffset, int yOffset) {
        return offset(image, xOffset, yOffset,
                0,0,0,0,0,0);
    }

    /**
     * Method to apply the position offset. This method provides pixel offsets, and individual color channels
     * @param image the BufferedImage inputted to have the offset applied to
     * @param xOffset the amount of pixels position should offset by in the X (Width) direction
     * @param yOffset the amount of pixels position should offset by in the Y (Height) direction
     * @param rxOffset the amount of pixels red channel position should offset by in the X (Width) direction
     * @param ryOffset the amount of pixels red channel position should offset by in the Y (Height) direction
     * @param gxOffset the amount of pixels green channel position should offset by in the X (Width) direction
     * @param gyOffset the amount of pixels green channel position should offset by in the Y (Height) direction
     * @param bxOffset the amount of pixels blue channel position should offset by in the X (Width) direction
     * @param byOffset the amount of pixels blue channel position should offset by in the Y (Height) direction
     * @return the BufferedImage with the position offsets applied
     */
    public static BufferedImage offset(BufferedImage image, int xOffset, int yOffset,
                                       int rxOffset, int ryOffset, int gxOffset,
                                       int gyOffset, int bxOffset, int byOffset) {

        int height = image.getHeight();
        int width =  image.getWidth();

        BufferedImage redChannel = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage greenChannel = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage blueChannel = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        offsetChannels(image, xOffset, yOffset,
                rxOffset, ryOffset, gxOffset, gyOffset, bxOffset, byOffset,
                height, width, redChannel, greenChannel, blueChannel);

        return combineChannels(height, width, redChannel, greenChannel, blueChannel);
    }

    /**
     * Method to offset the position of the individual color channels
     * @param image the BufferedImage inputted to have the offset applied to
     * @param xOffset the amount of pixels position should offset by in the X (Width) direction
     * @param yOffset the amount of pixels position should offset by in the Y (Height) direction
     * @param rxOffset the amount of pixels red channel position should offset by in the X (Width) direction
     * @param ryOffset the amount of pixels red channel position should offset by in the Y (Height) direction
     * @param gxOffset the amount of pixels green channel position should offset by in the X (Width) direction
     * @param gyOffset the amount of pixels green channel position should offset by in the Y (Height) direction
     * @param bxOffset the amount of pixels blue channel position should offset by in the X (Width) direction
     * @param byOffset the amount of pixels blue channel position should offset by in the Y (Height) direction
     * @param height the height (Y) of the original image in pixels
     * @param width the width (X) of the original image in pixels
     * @param redChannel the BufferedImage of the offset applied Red Channel
     * @param greenChannel the BufferedImage of the offset applied Green Channel
     * @param blueChannel the BufferedImage of the offset applied Blue Channel
     */
    private static void offsetChannels(BufferedImage image, int xOffset, int yOffset,
                                       int rxOffset, int ryOffset, int gxOffset,
                                       int gyOffset, int bxOffset, int byOffset,
                                       int height, int width,
                                       BufferedImage redChannel,
                                       BufferedImage greenChannel,
                                       BufferedImage blueChannel) {

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                int srcPixel = image.getRGB(x, y);
                int srcRed = (srcPixel >> 16) & 0xFF;
                int srcGreen = (srcPixel >> 8) & 0xFF;
                int srcBlue = srcPixel & 0xFF;

                Color srcColor = new Color(srcRed, srcGreen, srcBlue);

                // Apply Offset, if offset is negative, wrap to other side using modulo
                int rX = (((rxOffset + xOffset + x) % width) + width) % width;
                int rY = (((ryOffset + yOffset + y) % height) + height) % height;
                int gX = (((gxOffset + xOffset + x) % width) + width) % width;
                int gY = (((gyOffset + yOffset + y) % height) + height) % height;
                int bX = (((bxOffset + xOffset + x) % width) + width) % width;
                int bY = (((byOffset + yOffset + y) % height) + height) % height;

                redChannel.setRGB(rX, rY, srcColor.getRed() << 16);
                greenChannel.setRGB(gX, gY, srcColor.getGreen() << 8);
                blueChannel.setRGB(bX, bY, srcColor.getBlue());
            }
        }
    }

    /**
     * Method that takes in Color Channel BufferedImages and returns composite BufferedImage
     * @param height the height (Y) of the original image in pixels
     * @param width the width (X) of the original image in pixels
     * @param redChannel the BufferedImage of the offset applied Red Channel
     * @param greenChannel the BufferedImage of the offset applied Green Channel
     * @param blueChannel the BufferedImage of the offset applied Blue Channel
     * @return the BufferedImage with the position offsets applied
     */
    private static BufferedImage combineChannels(int height, int width,
                                                 BufferedImage redChannel,
                                                 BufferedImage greenChannel,
                                                 BufferedImage blueChannel) {

        BufferedImage compositeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the RGB values from the individual color channels
                int compRed = (redChannel.getRGB(x, y) >> 16) & 0xFF;
                int compGreen = (greenChannel.getRGB(x, y) >> 8) & 0xFF;
                int compBlue = blueChannel.getRGB(x, y) & 0xFF;

                // Combine the RGB values into a single pixel
                int compPixel = (compRed << 16) | (compGreen << 8) | compBlue;

                // Set the pixel in the composite image
                compositeImage.setRGB(x, y, compPixel);
            }
        }
        return compositeImage;
    }

    public static void main(String[] args) throws IOException {

        BufferedImage image = ImageIO.read(new File("C:\\Users\\Aaron\\Desktop\\0.jpg"));
        BufferedImage comp = offset(image, 0,0,32,32,0,0,0,0);
        ImageIO.write(comp, "jpg", new File("outputImage.jpg"));
    }
}
