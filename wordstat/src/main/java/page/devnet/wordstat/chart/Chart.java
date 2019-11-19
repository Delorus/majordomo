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

    //todo переделать на нормальный способ, сейчас все плохо из-за кучи копирований в памяти
    public InputStream toInputStream() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpeg", outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }
}
