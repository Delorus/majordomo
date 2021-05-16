package page.devnet.vertxtgbot.tgapi;

import io.netty.buffer.Unpooled;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.multipart.MultipartForm;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAudio;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import page.devnet.hacks.BufferPipedInputStream;

import java.io.File;
import java.util.List;

/**
 * @author maksim
 * @since 31.05.2020
 */
final class InputFileHelper {

    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String TEXT_PLAIN = "text/plain";

    private InputFileHelper() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void addTextFileToForm(MultipartForm form, InputFile file, String fileField, boolean addField) {
        addFileToMultipartForm(form, file, fileField, addField, false);
    }

    public static void addBinaryFileToForm(MultipartForm form, InputFile file, String fileField, boolean addField) {
        addFileToMultipartForm(form, file, fileField, addField, true);
    }

    private static void addFileToMultipartForm(MultipartForm form, InputFile file, String fileField, boolean addField, boolean isBinary) {
        if (file.isNew()) {
            if (file.getNewMediaFile() != null) {
                if (isBinary) {
                    form.binaryFileUpload(file.getMediaName(), file.getMediaName(),
                            file.getNewMediaFile().getAbsolutePath(), APPLICATION_OCTET_STREAM);
                } else {
                    form.textFileUpload(file.getMediaName(), file.getMediaName(),
                            file.getNewMediaFile().getAbsolutePath(), TEXT_PLAIN);
                }

            } else if (file.getNewMediaStream() != null) {
                var stream = file.getNewMediaStream();
                if (BufferPipedInputStream.isBufferInputStream(stream)) {
                    var buffer = Buffer.buffer(Unpooled.wrappedBuffer(BufferPipedInputStream.unwrapBuffer(stream)));
                    if (isBinary) {
                        form.binaryFileUpload(file.getMediaName(), file.getMediaName(), buffer, APPLICATION_OCTET_STREAM);
                    } else {
                        form.textFileUpload(file.getMediaName(), file.getMediaName(), buffer, TEXT_PLAIN);
                    }
                } else {
                    throw new UnsupportedOperationException("Upload file via InputStream is not supported to ensure non-blocking operations");
                }
            }
        }

        if (addField) {
            form.attribute(fileField, file.getAttachName());
        }
    }

    public static void addInputMediaToForm(MultipartForm form, List<InputMedia> media, String field) {
        for (InputMedia inputMedia : media) {
            addInputMediaToForm(form, inputMedia, "");
        }

        form.attribute(field, Json.encode(media));
    }

    public static void addInputMediaToForm(MultipartForm form, InputMedia media, String field) {
        if (media.isNewMedia()) {
            if (media.getNewMediaFile() != null) {
                File file = media.getNewMediaFile();
                form.binaryFileUpload(media.getMediaName(), media.getMediaName(), file.getAbsolutePath(), APPLICATION_OCTET_STREAM);
            } else if (media.getNewMediaStream() != null) {
                throw new UnsupportedOperationException("Upload media via InputStream is not supported to ensure non-blocking operations");
            }
        }

        if (media instanceof InputMediaAudio) {
            InputMediaAudio audio = (InputMediaAudio) media;
            if (audio.getThumb() != null) {
                addBinaryFileToForm(form, audio.getThumb(), InputMediaAudio.THUMB_FIELD, false);
            }
        } else if (media instanceof InputMediaDocument) {
            InputMediaDocument document = (InputMediaDocument) media;
            if (document.getThumb() != null) {
                addBinaryFileToForm(form, document.getThumb(), InputMediaDocument.THUMB_FIELD, false);
            }
        } else if (media instanceof InputMediaVideo) {
            InputMediaVideo video = (InputMediaVideo) media;
            if (video.getThumb() != null) {
                addBinaryFileToForm(form, video.getThumb(), InputMediaVideo.THUMB_FIELD, false);
            }
        }

        if (!field.isEmpty()) {
            form.attribute(field, Json.encode(media));
        }
    }
}
