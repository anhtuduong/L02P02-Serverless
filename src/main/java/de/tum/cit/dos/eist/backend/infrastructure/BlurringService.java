package de.tum.cit.dos.eist.backend.infrastructure;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

public class BlurringService {
    /**
     * Applies a blur effect to the given BufferedImage using a convolution kernel.
     * 
     * @param originalImage The original image to be blurred.
     * @return A new BufferedImage with the blur effect applied.
     */
    public BufferedImage applyBlur(BufferedImage originalImage) {
        BufferedImage blurredImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        int radius = 11;
        int size = radius * 2 + 1;

        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];

        Arrays.fill(data, weight);

        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);

        op.filter(originalImage, blurredImage);

        return blurredImage;
    }
}
