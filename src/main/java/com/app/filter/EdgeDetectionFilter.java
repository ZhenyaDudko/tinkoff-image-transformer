package com.app.filter;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class EdgeDetectionFilter {

    private EdgeDetectionFilter() {
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
        log.info("Applying edge detection filter concurrently");
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        int width = image.getWidth();
        int height = image.getWidth();

        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        BufferedImage resultImage =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final int[][] kernel = {{0, -1, 0}, {-1, 4, -1}, {0, -1, 0}};

        for (int x = 1; x < width - 1; x++) {
            int finalX = x;
            executor.execute(() -> {
                for (int y = 1; y < height - 1; y++) {
                    int sumR = 0;
                    int sumG = 0;
                    int sumB = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            Color pixel =
                                    new Color(image.getRGB(finalX + i, y + j));
                            sumR += kernel[i + 1][j + 1] * pixel.getRed();
                            sumG += kernel[i + 1][j + 1] * pixel.getGreen();
                            sumB += kernel[i + 1][j + 1] * pixel.getBlue();
                        }
                    }

                    final int newR = Math.min(Math.max(sumR, 0), 255);
                    final int newG = Math.min(Math.max(sumG, 0), 255);
                    final int newB = Math.min(Math.max(sumB, 0), 255);

                    int filteredColor = new Color(newR, newG, newB).getRGB();
                    resultImage.setRGB(finalX, y, filteredColor);
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
