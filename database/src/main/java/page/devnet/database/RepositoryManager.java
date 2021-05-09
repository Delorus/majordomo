package page.devnet.database;

import lombok.Getter;
import page.devnet.database.repository.UnsubscribeRepository;
import page.devnet.database.repository.UserRepository;
import page.devnet.database.repository.WordStorageRepository;
import page.devnet.database.repository.impl.UnsubscribeRepositoryImpl;
import page.devnet.database.repository.impl.UserRepositoryImpl;
import page.devnet.database.repository.impl.WordStorageImpl;

public class RepositoryManager {

    private final DataSource dataSource;

    @Getter
    private final UserRepository userRepository;

    @Getter
    private final UnsubscribeRepository unsubscribeRepository;

    @Getter
    private final WordStorageRepository wordStorageRepository;

    public RepositoryManager() {
        dataSource = new DataSource();
        unsubscribeRepository = new UnsubscribeRepositoryImpl(dataSource);
        userRepository = new UserRepositoryImpl(dataSource);
        wordStorageRepository = new WordStorageImpl(dataSource);
    }
}
