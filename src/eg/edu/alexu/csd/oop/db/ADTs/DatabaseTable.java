package eg.edu.alexu.csd.oop.db.ADTs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bassam on 11/18/2017.
 */
public class DatabaseTable {

    private String databaseName;
    private String tableName;
    private List<DatabaseColumn> tableColumns;

    public DatabaseTable(String tableName) {
        this.tableName = tableName;
        this.tableColumns = new ArrayList<>();
    }

    public int getNumberOfRows() {
        int maxNumberOfRows = 0;

        for (DatabaseColumn column : tableColumns) {
            int columnSize = column.getColumnElements().size();
            if (columnSize > maxNumberOfRows) {
                maxNumberOfRows = columnSize;
            }
        }
        return maxNumberOfRows;
    }

    public boolean addColumn(DatabaseColumn databaseColumn) {
        if (columnExists(databaseColumn.getColumnName())) {
            return false;
        }
        return tableColumns.add(databaseColumn);
    }

    public void addColumnAt(int index, DatabaseColumn databaseColumn) {
        if (columnExists(databaseColumn.getColumnName())) {
            return;
        }
        tableColumns.add(index, databaseColumn);
    }

    private boolean columnExists(String columnName) {
        for (DatabaseColumn column : tableColumns) {
            if (column.getColumnName().equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    public boolean addColumns(List<DatabaseColumn> databaseColumns) {
        for (DatabaseColumn column : databaseColumns) {
            if (columnExists(column.getColumnName())) {
                return false;
            }
        }
        return tableColumns.addAll(databaseColumns);
    }

    public boolean addColumnsAt(int index, List<DatabaseColumn> databaseColumns) {
        for (DatabaseColumn column : databaseColumns) {
            if (columnExists(column.getColumnName())) {
                return false;
            }
        }
        return tableColumns.addAll(index, databaseColumns);
    }

    public int removeColumn(String columnName) {
        return removeColumns(columnName);
    }

    public int removeColumns(String... columnNames) {

        int removedColumns = 0;

        for (String columnName : columnNames) {
            for (int i = 0; i < tableColumns.size(); i++) {
                if (tableColumns.get(i).getColumnName().equalsIgnoreCase(columnName)) {
                    tableColumns.remove(i);
                    removedColumns++;
                    break;
                }
            }
        }
        return removedColumns;
    }

    public DatabaseColumn getColumn(String columnName) {
        for (DatabaseColumn databaseColumn : tableColumns) {
            if (databaseColumn.getColumnName().equalsIgnoreCase(columnName)) {
                return databaseColumn;
            }
        }
        return null;
    }

    public List<DatabaseColumn> getColumns(String... columnsNames) {

        ArrayList<DatabaseColumn> databaseColumns = new ArrayList<>(columnsNames.length);

        for (String columnName : columnsNames) {
            for (DatabaseColumn databaseColumn : tableColumns) {
                if (databaseColumn.getColumnName().equalsIgnoreCase(columnName)) {
                    databaseColumns.add(databaseColumn);
                    break;
                }
            }
        }

        return databaseColumns;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<DatabaseColumn> getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(List<DatabaseColumn> tableColumns) {
        this.tableColumns = tableColumns;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
