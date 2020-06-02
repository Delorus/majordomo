package page.devnet.database;

import lombok.Getter;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class DataSource {

    @Getter
    private final DB database;

    public DataSource() {
        this.database = DBMaker.fileDB("/storage/devnetdb").make();
    }
}
