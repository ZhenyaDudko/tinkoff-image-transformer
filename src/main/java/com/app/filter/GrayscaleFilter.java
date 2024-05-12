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
public final class GrayscaleFilter {

    private GrayscaleFilter() {
    }

    /**
     * Apply filter.
     * @param imageData image.
     * @param mediaType image media type.
     * @return result image.
     * @throws IOException
     */
    public static byte[] applyFilter(
            final byte[] imageData,
            final String mediaType
    ) throws IOException {
        log.info("Applying grayscale filter concurrently");
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        int width = image.getWidth();
        int height = image.getWidth();

        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < pixels.length; i++) {
            int finalI = i;
            executor.execute(() -> {
                int p = pixels[finalI];

                final int a = (p >> 24) & 0xff;
                final int r = (p >> 16) & 0xff;
                final int g = (p >> 8) & 0xff;
                final int b = p & 0xff;

                final int avg = (r + g + b) / 3;

                p = (a << 24) | (avg << 16) | (avg << 8) | avg;

                pixels[finalI] = p;
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        image.setRGB(0, 0, width, height, pixels, 0, width);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String imageFormat = mediaType.contains("png") ? "png" : "jpeg";
        ImageIO.write(image, imageFormat, output);
        return output.toByteArray();
    }
}
