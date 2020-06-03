package page.devnet.database.repository.impl;

import org.mapdb.Serializer;
import page.devnet.database.DataSource;
import page.devnet.database.entity.User;
import page.devnet.database.repository.UserRepository;
import page.devnet.database.serializer.SerializerUser;

import java.util.Map;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private static final String TABLE_NAME = "user";

    private final DataSource dataSource;

    // key userId, value
    private final Map<Integer, User> table;

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.table = dataSource.getDatabase().hashMap(TABLE_NAME).keySerializer(Serializer.INTEGER)
                .valueSerializer(new SerializerUser()).createOrOpen();
    }

    @Override
    public Optional<User> find(Integer id) {
        return Optional.ofNullable(table.get(id));
    }


    @Override
    public User createOrUpdate(Integer id, User user) {
        table.put(id, user);
        dataSource.getDatabase().commit();
        return user;
    }

    @Override
    public User delete(Integer id) {
        User user = table.remove(id);
        dataSource.getDatabase().commit();

        return user;
    }
}
