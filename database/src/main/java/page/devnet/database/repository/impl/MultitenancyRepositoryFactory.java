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
public class MultitenancyRepositoryFactory implements RepositoryFactory {

    private final DataSource dataSource;
    private final String tenantId;

    public MultitenancyRepositoryFactory(DataSource dataSource, String tenantId) {
        this.dataSource = dataSource;
        this.tenantId = tenantId;
    }

    @Override
    public UnsubscribeRepository buildUnsubscribeRepository() {
        return new UnsubscribeRepositoryImpl(dataSource, tenantName(UnsubscribeRepositoryImpl.TABLE_NAME));
    }

    @Override
    public UserRepository buildUserRepository() {
        return new UserRepositoryImpl(dataSource, tenantName(UserRepositoryImpl.TABLE_NAME));
    }

    @Override
    public WordStorageRepository buildWordStorageRepository() {
        return new WordStorageImpl(dataSource, tenantName(WordStorageImpl.TABLE_DATE_TO_WORDS), tenantName(WordStorageImpl.TABLE_USER_TO_DATE));
    }

    private String tenantName(String tableName) {
        return this.tenantId + ":" + tableName;
    }
}
