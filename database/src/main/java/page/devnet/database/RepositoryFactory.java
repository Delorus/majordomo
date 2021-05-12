package page.devnet.database;

import page.devnet.database.repository.UnsubscribeRepository;
import page.devnet.database.repository.UserRepository;
import page.devnet.database.repository.WordStorageRepository;
import page.devnet.database.repository.impl.DefaultRepositoryFactory;
import page.devnet.database.repository.impl.MultitenancyRepositoryFactory;

public interface RepositoryFactory {

    static RepositoryFactory multitenancy(DataSource dataSource, String tenantId) {
        return new MultitenancyRepositoryFactory(dataSource, tenantId);
    }

    static RepositoryFactory simple(DataSource dataSource) {
        return new DefaultRepositoryFactory(dataSource);
    }

    UnsubscribeRepository buildUnsubscribeRepository();

    UserRepository buildUserRepository();

    WordStorageRepository buildWordStorageRepository();
}
