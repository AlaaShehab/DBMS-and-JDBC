package eg.edu.alexu.csd.oop.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import eg.edu.alexu.csd.oop.db.ADTs.DatabaseElement;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseEntries;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseRow;
import eg.edu.alexu.csd.oop.jdbc.abstract_classes.AbstractResultSet;

/**
 * Created by Bassam on 11/29/2017.
 */
public class DBResultSet extends AbstractResultSet {

    private DatabaseEntries entries;
    private int currentCursorIndex;
    private Statement statement;
    private ResultSetMetaData resultSetMetaData;
    private int numberOfEntries;

    public DBResultSet(Statement statement, DatabaseEntries entries) {
        this.entries = entries;
        this.currentCursorIndex = -1;
        this.statement = statement;
        this.numberOfEntries = entries.getNumberOfEntries();
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return getString(entries.getColumnName(columnIndex));
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        DatabaseElement element = getCurrentEntry().getElementOfColumn(columnLabel);
        if (element.getDataType() == DatabaseElement.DATATYPE_VARCHAR) {
            return (String) element.getValue();
        }
        throw new SQLException("Column data type isn't VARCHAR!");
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return getInt(entries.getColumnName(columnIndex));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        DatabaseElement element = getCurrentEntry().getElementOfColumn(columnLabel);
        if (element.getDataType() == DatabaseElement.DATATYPE_INT) {
            return (Integer) element.getValue();
        }
        throw new SQLException("Column data type isn't INT!");
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return getCurrentEntry()
                .getElementOfColumn(entries.getColumnName(columnIndex))
                .getValue();
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return entries.getColumnIndex(columnLabel);
    }

    @Override
    public void beforeFirst() throws SQLException {
        throwExceptionIfNullEntries("Null Entries");
        currentCursorIndex = -1;
    }

    @Override
    public void afterLast() throws SQLException {
        throwExceptionIfNullEntries("Null Entries");
        currentCursorIndex = numberOfEntries;
    }

    @Override
    public boolean first() throws SQLException {
        return absolute(1);
    }

    @Override
    public boolean last() throws SQLException {
        return absolute(-1);
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throwExceptionIfNullEntries("Null Entries");
        if (numberOfEntries == 0) {
            return false;
        }
        if (row == 0 || row < -numberOfEntries) {
            beforeFirst();
            return false;
        } else if (row > numberOfEntries) {
            afterLast();
            return false;
        } else if (row > 0) {
            currentCursorIndex = row - 1;
        } else {
            currentCursorIndex = numberOfEntries + row;
        }
        return true;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throwExceptionIfNullEntries("Null Entries");
        return currentCursorIndex < 0;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throwExceptionIfNullEntries("Null Entries");
        return currentCursorIndex >= numberOfEntries;
    }

    @Override
    public boolean isFirst() throws SQLException {
        throwExceptionIfNullEntries("Null Entries");
        return currentCursorIndex == 0;
    }

    @Override
    public boolean isLast() throws SQLException {
        throwExceptionIfNullEntries("Null Entries");
        return currentCursorIndex == numberOfEntries - 1;
    }

    @Override
    public Statement getStatement() throws SQLException {
        return statement;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return entries == null;
    }

    @Override
    public boolean next() throws SQLException {

        currentCursorIndex++;
        if (currentCursorIndex >= numberOfEntries) {
            currentCursorIndex = numberOfEntries;
            return false;
        }
        return true;
    }

    @Override
    public boolean previous() throws SQLException {
        throwExceptionIfNullEntries("Null Entries");
        currentCursorIndex--;
        if (currentCursorIndex <= -1) {
            currentCursorIndex = -1;
            return false;
        }
        return true;
    }

    @Override
    public void close() throws SQLException {
        if (entries != null) {
            this.resultSetMetaData = new DBResultSetMetaData(entries);
            this.entries = null;
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        if (resultSetMetaData == null && entries != null) {
            this.resultSetMetaData = new DBResultSetMetaData(entries);
        }
        return resultSetMetaData;
    }

    private DatabaseRow getCurrentEntry() throws SQLException {
        return entries.getEntryAt(currentCursorIndex);
    }

    private void throwExceptionIfNullEntries(String message) throws SQLException {
        if (isClosed()) {
            throw new SQLException(message);
        }
    }
}
