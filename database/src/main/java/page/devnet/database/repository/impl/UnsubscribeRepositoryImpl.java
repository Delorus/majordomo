package page.devnet.database.repository.impl;

import org.mapdb.Serializer;
import page.devnet.database.DataSource;
import page.devnet.database.repository.UnsubscribeRepository;

import java.util.Optional;
import java.util.Set;

public class UnsubscribeRepositoryImpl implements UnsubscribeRepository {

    private static final String TABLE_NAME = "unsubscribe";

    private final DataSource dataSource;

    private final Set<Integer> table;

    public UnsubscribeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.table = dataSource.getDatabase().hashSet(TABLE_NAME).serializer(Serializer.INTEGER).createOrOpen();
    }

    @Override
    public Optional<Integer> find(Integer id) {
        if (table.contains(id)) {
            return Optional.of(id);
        }

        return Optional.empty();
    }

    @Override
    public Integer createOrUpdate(Integer id, Integer entity) {
        table.add(id);
        return id;
    }

    @Override
    public Integer delete(Integer id) {
        table.remove(id);
        return id;
    }
}
