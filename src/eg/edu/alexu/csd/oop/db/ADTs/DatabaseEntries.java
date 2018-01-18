package eg.edu.alexu.csd.oop.db.ADTs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bassam on 11/29/2017.
 */
public class DatabaseEntries {

    private String tableName;
    private List<DatabaseRow> entriesList;
    private List<DatabaseColumn> columnsTypesList;

    public DatabaseEntries(String tableName) {
        this.tableName = tableName;
        this.entriesList = new ArrayList<>();
    }

    public boolean addEntry(DatabaseRow entry) {
        if (doesSameRowIndexExist(entry) || !isSameTable(entry)) {
            return false;
        }
        if (entriesList.size() == 0) {
            setColumnsTypesList(entry);
        }
        return entriesList.add(entry);
    }

    public DatabaseRow getEntryAt(int entryIndex) throws SQLException {

        if (entryIndex >= entriesList.size()) {
            throw new SQLException("Invalid entry index");
        }

        return entriesList.get(entryIndex);
    }

    public boolean removeEntryAt(int entryIndex) throws SQLException {

        if (entryIndex >= entriesList.size()) {
            throw new SQLException("Invalid entry index");
        }

        return entriesList.remove(entryIndex) != null;
    }

    public void setHasNoEntries(List<DatabaseColumn> columnsTypesList) {
        this.entriesList = new ArrayList<>();
        this.columnsTypesList = columnsTypesList;
    }

    public boolean hasNoEntries() {
        return entriesList.size() == 0;
    }

    public int getColumnIndex(String columnName) throws SQLException {
        for (int i = 0; i < columnsTypesList.size(); i++) {
            if (columnName.equalsIgnoreCase(columnsTypesList.get(i).getColumnName())) {
                return i;
            }
        }
        throw new SQLException("Invalid column label");
    }

    public String getColumnName(int columnIndex) throws SQLException {
        if (columnIndex >= columnsTypesList.size()) {
            throw new SQLException("Invalid column index");
        }
        return columnsTypesList.get(columnIndex).getColumnName();
    }

    public List<DatabaseColumn> getColumnsTypesList() {
        if (hasNoEntries()) {
            return columnsTypesList;
        }
        return columnsTypesList;
    }

    private void setColumnsTypesList(DatabaseRow entry) {
        columnsTypesList = entry.getColumnTypesList();
    }

    private boolean isSameTable(DatabaseRow entry) {
        return entry.getTableName().equalsIgnoreCase(tableName);
    }

    private boolean doesSameRowIndexExist(DatabaseRow entry) {
        for (DatabaseRow row : entriesList) {
            if (entry.getRowIndex() == row.getRowIndex()) {
                return true;
            }
        }
        return false;
    }

    public String getTableName() {
        return tableName;
    }

    public int getNumberOfEntries() {
        return entriesList.size();
    }

    public List<DatabaseRow> getEntriesList() {
        return entriesList;
    }
}
