package page.devnet.wordstat.chart;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author maksim
 * @since 23.03.19
 */
public final class Chart {

    private final BufferedImage bufferedImage;

    Chart(BufferedImage bufferedImage) {

        this.bufferedImage = bufferedImage;
    }

    public InputStream toInputStream() throws IOException {
        byte[] imageInBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            baos.flush();
            imageInBytes = baos.toByteArray();
        }
        return new ByteArrayInputStream(imageInBytes);
    }
}
