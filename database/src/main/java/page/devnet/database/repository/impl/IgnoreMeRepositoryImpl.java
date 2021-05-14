package page.devnet.database.repository.impl;

import org.mapdb.Serializer;
import page.devnet.database.DataSource;
import page.devnet.database.repository.IgnoreMeRepository;

import java.util.Set;

/**
 * @author sherb
 * @since 14.05.2021
 */
public class IgnoreMeRepositoryImpl implements IgnoreMeRepository {

    private final DataSource dataSource;
    private final Set<Integer> table;

    public IgnoreMeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.table = dataSource.getDatabase().hashSet("ignoreme").serializer(Serializer.INTEGER).createOrOpen();
    }

    @Override
    public boolean contains(Integer id) {
        return table.contains(id);
    }

    @Override
    public boolean add(Integer id) {
        var added = table.add(id);
        dataSource.getDatabase().commit();
        return added;
    }

    @Override
    public boolean remove(Integer id) {
        var removed = table.remove(id);
        dataSource.getDatabase().commit();
        return removed;
    }
}
