package page.devnet.database.serializer;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;

import javax.swing.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class SerializerListInstant extends GroupSerializerObjectArray <List<Instant>> {
    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull List<Instant> value) throws IOException {

    }

    @Override
    public List<Instant> deserialize(@NotNull DataInput2 input, int available) throws IOException {
        return null;
    }
}
