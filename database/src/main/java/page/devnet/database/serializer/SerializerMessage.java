package page.devnet.database.serializer;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;
import page.devnet.database.entity.Message;

import java.io.IOException;

public class SerializerMessage extends GroupSerializerObjectArray <Message> {
    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull Message value) throws IOException {
        out.writeUTF(value.getUserID());
        out.writeLong(value.getDate().toEpochMilli());

    }

    @Override
    public Message deserialize(@NotNull DataInput2 input, int available) throws IOException {
        return null;
    }
}
