package page.devnet.hacks;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author sherb
 * @since 16.05.2021
 */
class BufferPipedInputStreamTest {

    @Test
    public void dataTransferCorrectly() throws IOException {
        // Setup
        var stream = new BufferPipedInputStream();

        // When
        try (var out = stream.asOutputStream()) {
            out.write("hello world".getBytes(StandardCharsets.UTF_8));
        }

        // Then
        assertEquals("hello world", buf2str(BufferPipedInputStream.unwrapBuffer(stream.asInputStream())));
    }

    private static String buf2str(ByteBuffer buffer) {
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }
}
