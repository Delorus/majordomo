package page.devnet.database.serializer;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;
import page.devnet.database.entity.Message;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SerializerList extends GroupSerializerObjectArray <List<String>> {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull List<String> value) throws IOException {
        out.writeUTF(value.toString() == null? " ": value.toString());

    }

    @Override
    public List<String> deserialize(@NotNull DataInput2 input, int available) throws IOException {
        List<String> list = Arrays.asList(input.readUTF());
        return list;
    }
}
