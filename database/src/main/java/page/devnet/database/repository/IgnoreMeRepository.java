package page.devnet.database.repository;

/**
 * @author sherb
 * @since 14.05.2021
 */
public interface IgnoreMeRepository {
    boolean contains(Integer id);

    boolean add(Integer id);

    boolean remove(Integer id);
}
