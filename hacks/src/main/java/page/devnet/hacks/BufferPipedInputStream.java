package page.devnet.hacks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author sherb
 * @since 15.05.2021
 */
public class BufferPipedInputStream {

    public static boolean isBufferInputStream(InputStream stream) {
        return stream instanceof BufferByteArrayInputStream;
    }

    public static ByteBuffer unwrapBuffer(InputStream stream) {
        if (isBufferInputStream(stream)) {
            return ((BufferByteArrayInputStream) stream).buffer();
        }

        return ByteBuffer.allocate(0);
    }

    public static InputStream fromBytes(byte[] data) {
        BufferPipedInputStream stream = new BufferPipedInputStream();
        stream.outputStream.writeBytes(data);
        return stream.asInputStream();
    }

    private final BufferByteArrayOutputStream outputStream = new BufferByteArrayOutputStream();

    public OutputStream asOutputStream() {
        return outputStream;
    }

    public InputStream asInputStream() {
        if (outputStream.size() == 0) {
            return new ByteArrayInputStream(new byte[0]);
        }

        return new BufferByteArrayInputStream(outputStream.buffer(), 0, outputStream.size());
    }

    private static class BufferByteArrayOutputStream extends ByteArrayOutputStream {

        public BufferByteArrayOutputStream() {
            super();
        }

        public BufferByteArrayOutputStream(int size) {
            super(size);
        }

        public byte[] buffer() {
            return this.buf;
        }
    }

    private static class BufferByteArrayInputStream extends ByteArrayInputStream {

        private final ByteBuffer buffer;

        public BufferByteArrayInputStream(byte[] buf) {
            super(buf);
            buffer = ByteBuffer.wrap(buf);
        }

        public BufferByteArrayInputStream(byte[] buf, int offset, int length) {
            super(buf, offset, length);
            buffer = ByteBuffer.wrap(buf, offset, length);
        }

        public ByteBuffer buffer() {
            return buffer;
        }
    }
}
