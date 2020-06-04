package page.devnet.database.serializer;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;

import java.io.IOException;
import java.time.Instant;

public class SerializerInstant extends GroupSerializerObjectArray <Instant> {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull Instant value) throws IOException {
        out.writeLong(value.toEpochMilli());
    }

    @Override
    public Instant deserialize(@NotNull DataInput2 input, int available) throws IOException {

        return  Instant.ofEpochMilli(input.readLong());
    }
}
