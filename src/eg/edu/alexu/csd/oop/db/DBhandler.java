package eg.edu.alexu.csd.oop.db;

import eg.edu.alexu.csd.oop.db.ADTs.DatabaseManager;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseTable;

public interface DBhandler {
    boolean saveDB(DatabaseManager database);

    DatabaseTable loadTable(String tableName, String databaseName);

    boolean saveTable(DatabaseTable table);

    boolean createDTD(DatabaseTable dbtable);

    boolean databaseExists(String databaseName);

    String getPath(String databaseName);

    DatabaseManager loadDB(String databaseName);

    boolean deleteDB(String databaseName);

    boolean deleteTable(String tableName, String databaseName);
}
