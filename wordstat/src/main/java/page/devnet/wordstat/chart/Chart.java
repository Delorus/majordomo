package page.devnet.wordstat.chart;

import page.devnet.hacks.BufferPipedInputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
        var pipe = new BufferPipedInputStream();
        try (var out = pipe.asOutputStream()) {
            ImageIO.write(bufferedImage, "png", out);
        }

        return pipe.asInputStream();
    }
}
