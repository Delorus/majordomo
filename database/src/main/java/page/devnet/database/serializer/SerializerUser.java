package page.devnet.database.serializer;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.serializer.GroupSerializerObjectArray;
import page.devnet.database.entity.User;

import java.io.IOException;

public class SerializerUser extends GroupSerializerObjectArray<User> {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull User value) throws IOException {
        out.writeUTF(value.getFirstName() == null ? "" : value.getFirstName());
        out.writeUTF(value.getLastName() == null ? "" : value.getLastName());
        out.writeUTF(value.getUserName() == null ? "" : value.getUserName());
    }

    @Override
    public User deserialize(@NotNull DataInput2 input, int available) throws IOException {
        User user = new User();
        user.setFirstName(input.readUTF());
        user.setLastName(input.readUTF());
        user.setUserName(input.readUTF());
        return user;
    }
}