package eg.edu.alexu.csd.oop.db.ADTs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bassam on 11/29/2017.
 */
public class DatabaseRow {

    private String tableName;
    private int rowIndex;
    private Map<String, DatabaseElement> elementsOfTheRow;

    private DatabaseRow(DatabaseTable table, int rowIndex, String... columnNames) {

        this.elementsOfTheRow = new LinkedHashMap<>();
        this.tableName = table.getTableName();

        if (!(rowIndex >= table.getNumberOfRows())) {
            this.rowIndex = rowIndex;
            int currentColumnIndex = 0;
            for (DatabaseColumn column : table.getTableColumns()) {
                if (columnNames == null) {
                    elementsOfTheRow.put(column.getColumnName(), column.getColumnElements().get(rowIndex));
                    continue;
                }
                for (String columnName : columnNames) {
                    if (column.getColumnName().equalsIgnoreCase(columnName)) {
                        elementsOfTheRow.put(column.getColumnName(), column.getColumnElements().get(rowIndex));
                        break;
                    }
                }
            }
        } else {
            throw new IndexOutOfBoundsException("Row index is beyond number of entries");
        }
    }

    public static DatabaseRow getRowAtIndex(DatabaseTable table, int rowIndex) {
        return new DatabaseRow(table, rowIndex, null);
    }

    public static DatabaseRow getRowAtIndex(DatabaseTable table, int rowIndex, String... columnNames) {
        return new DatabaseRow(table, rowIndex, columnNames);
    }

    public DatabaseElement getElementOfColumn(String columnName) {
        return elementsOfTheRow.get(columnName);
    }

    public Map<String, DatabaseElement> getElementsOfTheRow() {
        return elementsOfTheRow;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getTableName() {
        return tableName;
    }

    public List<DatabaseColumn> getColumnTypesList() {
        List<DatabaseColumn> columnTypesList = new ArrayList<>();
        for (String columnName : elementsOfTheRow.keySet()) {
            columnTypesList.add(new DatabaseColumn(columnName, elementsOfTheRow.get(columnName).getDataType()));
        }
        return columnTypesList;
    }
}
