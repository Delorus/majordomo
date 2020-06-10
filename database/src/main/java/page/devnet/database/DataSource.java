package page.devnet.database;

import lombok.Getter;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class DataSource {

    @Getter
    private final DB database;

    public DataSource() {

        //for the memoryDb need DBMaker.memoryDB().transactionEnable().make();
        this.database = DBMaker.fileDB("/storage/devnetdb").transactionEnable().make();
    }
}
