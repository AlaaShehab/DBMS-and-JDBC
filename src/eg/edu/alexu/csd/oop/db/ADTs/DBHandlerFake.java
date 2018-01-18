package eg.edu.alexu.csd.oop.db.ADTs;

import eg.edu.alexu.csd.oop.db.DBhandler;

import java.util.HashMap;
import java.util.Map;

public class DBHandlerFake implements DBhandler {

    Map<String, DatabaseManager> fakeDatabases = new HashMap<>();


    @Override
    public boolean saveDB(DatabaseManager database) {
        fakeDatabases.put(database.getDatabaseName().toUpperCase(), database);
        return true;
    }

    @Override
    public boolean saveTable(DatabaseTable table) {

        return true;
    }

    @Override
    public boolean createDTD(DatabaseTable dbtable) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public DatabaseTable loadTable(String tableName, String databaseName) {
        // TODO Auto-generated method stub
        DatabaseManager db = fakeDatabases.get(databaseName.toUpperCase());
        return db.getDatabaseTables(tableName).get(0);

    }

    @Override
    public boolean databaseExists(String databaseName) {
        return fakeDatabases.containsKey(databaseName.toUpperCase());
    }

    @Override
    public String getPath(String databaseName) {
        return databaseName;
    }

    @Override
    public DatabaseManager loadDB(String databaseName) {
        return fakeDatabases.get(databaseName.toUpperCase());
    }

    @Override
    public boolean deleteDB(String databaseName) {
        fakeDatabases.remove(databaseName.toUpperCase());
        return true;
    }

    @Override
    public boolean deleteTable(String tableName, String databaseName) {
        DatabaseManager db = fakeDatabases.get(databaseName.toUpperCase());
        return db.removeTable(tableName) > 0;

    }


}
