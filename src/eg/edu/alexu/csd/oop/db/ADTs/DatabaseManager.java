package eg.edu.alexu.csd.oop.db.ADTs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bassam on 11/18/2017.
 */
public class DatabaseManager {

    private String databaseName;
    private List<DatabaseTable> databaseTables;

    public DatabaseManager(String databaseName) {
        this.databaseName = databaseName;
        databaseTables = new ArrayList<DatabaseTable>();
    }

    public boolean addTable(DatabaseTable databaseTable) {
        if (tableExists(databaseTable.getTableName())) {
            return false;
        }
        return databaseTables.add(databaseTable);
    }

    public boolean addTables(List<DatabaseTable> databaseTables) {
        for (DatabaseTable table : databaseTables) {
            if (tableExists(table.getTableName())) {
                return false;
            }
        }
        return this.databaseTables.addAll(databaseTables);
    }

    public int removeTable(String tableName) {
        return removeTables(tableName);
    }

    private int removeTables(String... tablesNames) {

        int removedTables = 0;
        for (String tableName : tablesNames) {
            for (int i = 0; i < databaseTables.size(); i++) {
                if (databaseTables.get(i).getTableName().equalsIgnoreCase(tableName)) {
                    databaseTables.remove(i);
                    removedTables++;
                    break;
                }
            }
        }
        return removedTables;
    }

    private boolean tableExists(String tableName) {
        for (DatabaseTable table : databaseTables) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public List<DatabaseTable> getDatabaseTables(String... tableNames) {
        List<DatabaseTable> databaseTablesToReturn = new ArrayList<>(tableNames.length);
        for (String tableName : tableNames) {
            for (int i = 0; i < databaseTables.size(); i++) {
                DatabaseTable currentTable = databaseTables.get(i);
                if (tableName.equalsIgnoreCase(currentTable.getTableName())) {
                    databaseTablesToReturn.add(currentTable);
                }
            }
        }
        return databaseTablesToReturn;
    }

    public List<DatabaseTable> getAllDatabaseTables() {
        return databaseTables;
    }

    public void setDatabaseTables(List<DatabaseTable> databaseTables) {
        this.databaseTables = databaseTables;
    }
}
