package com.app.filter;

import java.io.IOException;

@FunctionalInterface
public interface FilterFunction {
    /**
     * Apply filter.
     * @param image image.
     * @param mediaType image media type.
     * @return result image.
     * @throws IOException
     */
    byte[] applyFilter(byte[] image, String mediaType) throws IOException;
}
