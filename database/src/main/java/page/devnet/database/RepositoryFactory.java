package page.devnet.database;

import page.devnet.database.repository.UnsubscribeRepository;
import page.devnet.database.repository.UserRepository;
import page.devnet.database.repository.WordStorageRepository;
import page.devnet.database.repository.impl.UnsubscribeRepositoryImpl;
import page.devnet.database.repository.impl.UserRepositoryImpl;
import page.devnet.database.repository.impl.WordStorageImpl;

public class RepositoryFactory {

    private final DataSource dataSource;

    public RepositoryFactory(DataSource dataSource) {
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
