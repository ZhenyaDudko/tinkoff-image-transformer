package com.app.filter;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class BlurFilter {

    private BlurFilter() {
    }

    /**
     * Apply filter.
     * @param imageData image.
     * @param mediaType media type.
     * @return result image.
     * @throws IOException
     */
    public static byte[] applyFilter(
            final byte[] imageData,
            final String mediaType
    ) throws IOException {
        log.info("Applying blur filter concurrently");
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        int width = image.getWidth();
        int height = image.getWidth();

        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        BufferedImage resultImage
                = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 1; y < height - 1; y++) {
            int finalY = y;
            executor.execute(() -> {
                for (int x = 1; x < width - 1; x++) {
                    int sumRed = 0;
                    int sumGreen = 0;
                    int sumBlue = 0;

                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int rgb = image.getRGB(x + dx, finalY + dy);
                            sumRed += (rgb >> 16) & 0xFF;
                            sumGreen += (rgb >> 8) & 0xFF;
                            sumBlue += rgb & 0xFF;
                        }
                    }

                    int avgRed = sumRed / 9;
                    int avgGreen = sumGreen / 9;
                    int avgBlue = sumBlue / 9;

                    int blurredValue
                            = (avgRed << 16) + (avgGreen << 8) + avgBlue;
                    resultImage.setRGB(x, finalY, blurredValue);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String imageFormat = mediaType.contains("png") ? "png" : "jpeg";
        ImageIO.write(resultImage, imageFormat, output);
        return output.toByteArray();
    }
}
