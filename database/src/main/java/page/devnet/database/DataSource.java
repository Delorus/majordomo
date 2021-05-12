package page.devnet.database;

import lombok.Getter;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class DataSource {

    public static DataSource inMemory() {
        return new DataSource(true);
    }

    @Getter
    private final DB database;

    public DataSource() {
        this(false);
    }

    private DataSource(boolean inMemory) {
        this.database = inMemory
                ? DBMaker.memoryDB().transactionEnable().make()
                : DBMaker.fileDB("/storage/devnetdb").transactionEnable().make();
    }
}
