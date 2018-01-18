/**
 *
 */
package eg.edu.alexu.csd.oop.db.cs17;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eg.edu.alexu.csd.oop.DBparser.SQLparser;
import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseColumn;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseElement;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseEntries;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseManager;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseRow;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseTable;
import eg.edu.alexu.csd.oop.db.ADTs.DbhandlerImp;

/**
 * @author Personal
 */
public class DatabaseImplement implements Database {
    public DatabaseManager database;
    public String dbPath = null;
    DbhandlerImp databaseHandler = new DbhandlerImp();
    private DatabaseEntries resultedRows;
    int row = 0, colm = 0;

    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
        if (!databaseHandler.databaseExists(databaseName) && dropIfExists) {
            try {
                executeStructureQuery("CREATE DATABASE " + databaseName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return databaseHandler.getPath(databaseName);
        } else if (databaseHandler.databaseExists(databaseName) && dropIfExists) {
            try {
            	 executeStructureQuery("DROP DATABASE " + databaseName);
                 executeStructureQuery("CREATE DATABASE " + databaseName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return databaseHandler.getPath(databaseName);
        } else if (!databaseHandler.databaseExists(databaseName) && !dropIfExists) {
            try {
                executeStructureQuery("CREATE DATABASE " + databaseName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return databaseHandler.getPath(databaseName);
        } else if (databaseHandler.databaseExists(databaseName) && !dropIfExists) {
            database = databaseHandler.loadDB(databaseName);
            return databaseHandler.getPath(databaseName);
        }

        return null;
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {

        SQLparser parser = new SQLparser();
        if (!parser.checkSyntax(query)) {
       	 throw new SQLException("Failed to execute : SQL Syntax Error "
            		+ "\n" + query);
        }
        if (parser.getStatement().equalsIgnoreCase("DROP")) {
        	if (parser.getTableName() != null) {
        		return dropTable(parser.getTableName(), database.getDatabaseName());
        	}
        	return dropDB(parser.getDBname());

        } else if (parser.getStatement().equalsIgnoreCase("CREATE")) {

        	if (parser.getTableName() != null) {
//        		if (database == null) {
//        			return false;
//        		}
        		return createTable(parser.getTableName(), database.getDatabaseName(), parser);
        	} else if (parser.getDBname() != null) {
            	return createDB(parser.getDBname());

        	}

        }

        return false;
    }


    @Override
    public Object[][] executeQuery(String query) throws SQLException {
    	if (database == null) {
    		throw new SQLException("Failed to execute : No database directory from databaseImplement"
            		+ "\n" + query);
    	}

        SQLparser parser = new SQLparser();

        if (!parser.checkSyntax(query)) {
            throw new SQLException("Failed to execute : SQL Syntax Error "
            		+ "\n" + query);
        }

        if (!parser.getStatement().equalsIgnoreCase("SELECT")) {
            throw new SQLException("Failed to execute : Wrong Statement Query "
            		+ "\n" + query);

        }

        String tableName = parser.getTableName();

        if (database.getDatabaseTables(tableName).isEmpty()) {
            throw new SQLException("Failed to execute : Table does not exist"
            		+ "\n" + query);
        }

        DatabaseTable table = database.getDatabaseTables(tableName).get(0);

        List<DatabaseColumn> selectedColumns = getSelectedColumns(table, parser);

        if (selectedColumns.isEmpty()) {
        	throw new SQLException("Failed to execute : Column does not exist"
            		+ "\n" + query);
        }

        ArrayList<String> condition = parser.getCondition();
        HashSet<Integer> selectedRows = getAffectedRows(table, parser);


        if (selectedRows.isEmpty()) {
        	resultedRows.setHasNoEntries(selectedColumns);
        }
        Object[][] result = new Object[selectedRows.size()][selectedColumns.size()];

        int totalRowsNumber = selectedColumns.get(0).getColumnElements().size();

        for (int i = 0; i < selectedColumns.size(); i++) {
            int counter = 0;
            for (int j = 0; j < totalRowsNumber; j++) {
                if (selectedRows.contains(j)) {
                    result[counter++][i] = selectedColumns.get(i).getColumnElements().get(j).getValue();
                }
            }
        }
        return result;

    }

    public int[] getDimension () {
    	 int[] r = new int[2];
    	 r[0] = row;
    	 r[1] = colm;
    	 return r;
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
    	if (database == null) {
    		throw new SQLException("Failed to execute : No database directory from databaseImplement"
            		+ "\n" + query + "\n" + database);
    	}

    	SQLparser parser = new SQLparser();

        if (!parser.checkSyntax(query)) {
            throw new SQLException("Failed to execute : SQL Syntax Error "
            		+ "\n" + query);
        }

        String tableName = parser.getTableName();

        if (database.getDatabaseTables(tableName).isEmpty()) {
            throw new SQLException("Failed to execute : Table does not exist "
            		+ "\n" + query);
        }

        String statement = parser.getStatement();

        if (statement.equalsIgnoreCase("DELETE")) {
            return delete(tableName, parser);
        } else if (statement.equalsIgnoreCase("UPDATE")) {
            return update(tableName, parser);
        } else if (statement.equalsIgnoreCase("INSERT")) {
            return insert(tableName, parser);
        } else {
        	throw new SQLException("Failed to execute : Wrong Statement Query "
            		+ "\n" + query);
        }
    }

    private int update(String tableName, SQLparser parser) throws SQLException {

        ArrayList<String> selectedColumnsNames = parser.getColumnsNames();
        ArrayList<String> selectedColumnValues = parser.getColumnsValues();
        ArrayList<String> condition = parser.getCondition();

        DatabaseTable table = database.getDatabaseTables(tableName).get(0);

        if (!ColumnsExist(table, selectedColumnsNames)) {
        	throw new SQLException("Failed to update table : Columns doesn't exist");
        }
        HashSet<Integer> selectedRows = getAffectedRows(table, parser);


        if (!correctType(table, parser)) {
            return 0;
        }

        int maxRowsInAColumn = table.getNumberOfRows();

        for (int i = 0; i < selectedColumnsNames.size(); i++) {
            DatabaseColumn currentColumn = table.getColumn(selectedColumnsNames.get(i));
            int type = currentColumn.getColumnDataType();
            for (int j = 0; j < maxRowsInAColumn; j++) {
                if (selectedRows.contains(j)) {
                    if (IsDigit(selectedColumnValues.get(i)) && type == 0) {
                        currentColumn.deleteElementAt(j);
                        int value = Integer.parseInt(selectedColumnValues.get(i));
                        DatabaseElement<Integer> element = new DatabaseElement<Integer>(value, 0);
                        currentColumn.addElementAt(j, element);
                    } else if (!IsDigit(selectedColumnValues.get(i)) && type == 1) {
                        currentColumn.deleteElementAt(j);
                        DatabaseElement<String> element = new DatabaseElement<String>(selectedColumnValues.get(i), 1);
                        currentColumn.addElementAt(j, element);
                    }
                }
            }
        }
        databaseHandler.saveTable(table);
        return selectedRows.size();
    }

    private boolean ColumnsExist (DatabaseTable table, ArrayList<String> selectedColumnsNames) {
    	for (int i = 0; i < selectedColumnsNames.size(); i++) {
    		if (table.getColumn(selectedColumnsNames.get(i)) == null) {
    			return false;
    		}
    	}
    	return true;
    }

    private boolean correctType(DatabaseTable table, SQLparser parser) {
        ArrayList<String> selectedColumnsNames = parser.getColumnsNames();
        ArrayList<String> selectedColumnValues = parser.getColumnsValues();

        List<DatabaseColumn> selectedColumns;
        if (selectedColumnsNames.isEmpty()) {
            selectedColumns = table.getTableColumns();
        } else {
            String[] temp = new String[selectedColumnsNames.size()];
            for (int i = 0; i < selectedColumnsNames.size(); i++) {
                temp[i] = selectedColumnsNames.get(i);
            }
            selectedColumns = table.getColumns(temp);
        }
        int size = selectedColumns.size();
        for (int i = 0; i < size; i++) {
            int typeColumn = selectedColumns.get(i).getColumnDataType();
            if (typeColumn == 0 && !IsDigit(selectedColumnValues.get(i))) {
                return false;
            }
        }
        return true;
    }


    private int delete(String tableName, SQLparser parser) {
        ArrayList<String> selectedColumnsNames = parser.getColumnsNames();
        ArrayList<String> condition = parser.getCondition();

        DatabaseTable table = database.getDatabaseTables(tableName).get(0);
        List<DatabaseColumn> tableColumns = table.getTableColumns();

        int rowSize = tableColumns.get(0).getColumnElements().size();

        if (selectedColumnsNames.isEmpty() && condition.isEmpty()) {
            int numberOfColumns = tableColumns.size();
            for (int i = 0; i < numberOfColumns; i++) {
                tableColumns.get(i).deleteAllEntries();
            }
            databaseHandler.saveTable(table);
            return rowSize;
        } else if (selectedColumnsNames.isEmpty() && !condition.isEmpty()) {

            HashSet<Integer> selectedRows = getAffectedRows(table, parser);

            for (int i = 0; i < tableColumns.size(); i++) {
                for (int j = rowSize - 1; j >= 0; j--) {
                    if (selectedRows.contains(j)) {
                        tableColumns.get(i).deleteElementAt(j);
                    }
                }
            }
            databaseHandler.saveTable(table);
            return selectedRows.size();
        } else if (!selectedColumnsNames.isEmpty() && condition.isEmpty()) {
            for (int i = 0; i < selectedColumnsNames.size(); i++) {
                table.getColumn(selectedColumnsNames.get(i)).setAllEntriesNull();
            }
            databaseHandler.saveTable(table);
            return rowSize;
        } else if (!selectedColumnsNames.isEmpty() && !condition.isEmpty()) {

            HashSet<Integer> selectedRows = getAffectedRows(table, parser);

            for (int i = 0; i < selectedColumnsNames.size(); i++) {
                for (int j = 0; j < rowSize; j++) {
                    if (selectedRows.contains(j)) {
                        table.getColumn(selectedColumnsNames.get(i)).deleteElementAt(j);
                        if (table.getColumn(selectedColumnsNames.get(i)).getColumnDataType() == 1) {
                            DatabaseElement<String> element = new DatabaseElement<String>(null, 1);
                            table.getColumn(selectedColumnsNames.get(i)).addElementAt(j, element);
                        } else {
                            DatabaseElement<Integer> element = new DatabaseElement<Integer>(null, 0);
                            table.getColumn(selectedColumnsNames.get(i)).addElementAt(j, element);
                        }
                    }

                }
            }
            databaseHandler.saveTable(table);
            return selectedRows.size();
        }

        return 0;
    }


    private int insert(String tableName, SQLparser parser) throws SQLException {

        ArrayList<String> selectedColumnsValues = parser.getColumnsValues();

        DatabaseTable table = database.getDatabaseTables(tableName).get(0);

        if (!correctType(table, parser)) {
            return 0;
        }

        List<DatabaseColumn> selectedColumns = getSelectedColumns(table, parser);
        if (selectedColumns.size() != selectedColumnsValues.size()) {
            return 0;
        }
        if (selectedColumns.isEmpty()) {
        	throw new SQLException("Failed to execute : Column does not exist");
        }

        int totalColumnNum = table.getTableColumns().size();

        for (int i = 0; i < totalColumnNum; i++) {

            boolean columnFound = false;
            DatabaseColumn currentColumn = table.getTableColumns().get(i);
            String name = currentColumn.getColumnName();
            int type = currentColumn.getColumnDataType();

            for (int j = 0; j < selectedColumns.size(); j++) {
                if (name.equals(selectedColumns.get(j).getColumnName())) {
                    if (type == 0) {
                        int value = Integer.valueOf(selectedColumnsValues.get(j));
                        DatabaseElement<Integer> element = new DatabaseElement<Integer>(value, 0);
                        currentColumn.addElement(element);
                    } else {
                        String value = selectedColumnsValues.get(j);
                        DatabaseElement<String> element = new DatabaseElement<String>(value, 1);
                        currentColumn.addElement(element);
                    }
                    columnFound = true;
                }
            }
            if (!columnFound) {
                if (type == 0) {
                    DatabaseElement<Integer> element = new DatabaseElement<Integer>(null, 0);
                    currentColumn.addElement(element);
                } else {
                    DatabaseElement<String> element = new DatabaseElement<String>(null, 1);
                    currentColumn.addElement(element);
                }
            }
        }
        databaseHandler.saveTable(table);
        return 1;
    }


    private boolean IsDigit(String input) {
        if (input == null) {
            return false;
        }
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isDigit(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    private boolean conditionSatisfied(Object object, String condition, String ConstCondition) {

        if (object == null) {
            return ConstCondition == null;
        }

        if (condition.equals("=")) {
            if (IsDigit(ConstCondition)
                    && (Integer) object == Integer.parseInt(ConstCondition)) {
                return true;
            } else if (String.valueOf(object).equalsIgnoreCase(ConstCondition)) {
                return true;
            }
        } else if (condition.equals(">")) {
            if (IsDigit(ConstCondition)
                    && (Integer) object > Integer.parseInt(ConstCondition)) {
                return true;
            }

        } else if (condition.equals("<")) {
            if (IsDigit(ConstCondition)
                    && (Integer) object < Integer.parseInt(ConstCondition)) {
                return true;
            }
        }
        return false;
    }


    private HashSet<Integer> getAffectedRows(DatabaseTable table, SQLparser parser) {

        resultedRows = new DatabaseEntries(table.getTableName());
        HashSet<Integer> selectedRows = new HashSet<Integer>();
        int columnSize = table.getNumberOfRows();

        ArrayList<String> condition = parser.getCondition();
        ArrayList<String> selectedCol = parser.getColumnsNames();

        if (selectedCol.isEmpty()) {
        	for (int i = 0; i < table.getTableColumns().size(); i++) {
        		selectedCol.add(table.getTableColumns().get(i).getColumnName());
        	}
        }

        String[] col = new String[selectedCol.size()];
        for (int i = 0; i < selectedCol.size(); i++) {
        	col[i] = selectedCol.get(i);
        }

        if (condition.isEmpty()) {
            for (int i = 0; i < columnSize; i++) {
            	resultedRows.addEntry(DatabaseRow.getRowAtIndex(table, i, col));
                selectedRows.add(i);
            }
            row = selectedRows.size();
            colm = selectedCol.size();
            return selectedRows;
        }


        if (table.getColumn(condition.get(0)) == null) {
        	 return selectedRows;
        }
        List<DatabaseElement> conditionColumnElements = table.getColumn(condition.get(0)).getColumnElements();

        for (int i = 0; i < columnSize; i++) {
            if (conditionSatisfied(conditionColumnElements.get(i).getValue(), condition.get(1), condition.get(2))) {
            	resultedRows.addEntry(DatabaseRow.getRowAtIndex(table, i, col));
                selectedRows.add(i);
            }
        }
        row = selectedRows.size();
        colm = selectedCol.size();
        return selectedRows;
    }

    public DatabaseEntries getResultInRows() {
    	return resultedRows;
    }

    private List<DatabaseColumn> getSelectedColumns(DatabaseTable table, SQLparser parser) {
        ArrayList<String> selectedColumnsNames = parser.getColumnsNames();
        if (selectedColumnsNames.isEmpty()) {
            return table.getTableColumns();
        }
        String[] temp = new String[selectedColumnsNames.size()];
        for (int i = 0; i < selectedColumnsNames.size(); i++) {
            temp[i] = selectedColumnsNames.get(i);
        }
        return table.getColumns(temp);
    }


    private boolean dropDB(String databaseName) {
    	 boolean removed = databaseHandler.deleteDB(databaseName);
         if (removed) {
             database = null;
         }
         return removed;

    }


    private boolean dropTable(String tableName, String databaseName) {
    	boolean removed = database.removeTable(tableName) > 0;
        if (removed) {
            databaseHandler.deleteTable(tableName, databaseName);
        }
        return removed;

    }


    private boolean createDB(String databaseName) {
//    	if (databaseHandler.databaseExists(databaseName)) {
//            return false;
//       }
//        database = new DatabaseManager(databaseName);
//        databaseHandler.saveDB(database);
//        return true;

        database = new DatabaseManager(databaseName);
        databaseHandler.saveDB(database);
        return true;
    }


    private boolean createTable(String tableName, String databaseName, SQLparser parse) throws SQLException {
        DatabaseTable table = new DatabaseTable(tableName);
        table.setDatabaseName(databaseName);
        ArrayList<String> columns = parse.getColumnsNames();
        ArrayList<Integer> columnTypes = parse.getColumnsTypes();

        ArrayList<DatabaseColumn> columnsList = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            columnsList.add(new DatabaseColumn(columns.get(i), columnTypes.get(i)));
        }

        table.addColumns(columnsList);
        if (database.addTable(table)) {
            databaseHandler.saveTable(table);
            return true;

        }
        return false;
    }

}
