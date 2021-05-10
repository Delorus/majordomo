package page.devnet.database.repository.impl;

import page.devnet.database.DataSource;
import page.devnet.database.RepositoryFactory;
import page.devnet.database.repository.UnsubscribeRepository;
import page.devnet.database.repository.UserRepository;
import page.devnet.database.repository.WordStorageRepository;

/**
 * @author sherb
 * @since 10.05.2021
 */
public class DefaultRepositoryFactory implements RepositoryFactory {
    private final DataSource dataSource;

    public DefaultRepositoryFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public UnsubscribeRepository buildUnsubscribeRepository() {
        return new UnsubscribeRepositoryImpl(dataSource);
    }

    public UserRepository buildUserRepository() {
        return new UserRepositoryImpl(dataSource);
    }

    public WordStorageRepository buildWordStorageRepository() {
        return new WordStorageImpl(dataSource);
    }
}
