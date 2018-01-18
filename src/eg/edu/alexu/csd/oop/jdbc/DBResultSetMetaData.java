package eg.edu.alexu.csd.oop.jdbc;

import eg.edu.alexu.csd.oop.db.ADTs.DatabaseColumn;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseElement;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseEntries;
import eg.edu.alexu.csd.oop.jdbc.abstract_classes.AbstractResultSetMetaData;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created by Bassam on 11/29/2017.
 */
public class DBResultSetMetaData extends AbstractResultSetMetaData {

    private String tableName;
    private List<DatabaseColumn> tableColumnsTypes;


    public DBResultSetMetaData(DatabaseEntries entries) {

        this.tableName = entries.getTableName();
        this.tableColumnsTypes = entries.getColumnsTypesList();
    }

    @Override
    public int getColumnCount() throws SQLException {
        return tableColumnsTypes.size();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getColumnName(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return tableColumnsTypes.get(column).getColumnName();
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return tableName;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        switch (tableColumnsTypes.get(column).getColumnDataType()) {
            case DatabaseElement.DATATYPE_INT:
                return Types.INTEGER;
            case DatabaseElement.DATATYPE_VARCHAR:
                return Types.VARCHAR;
            default:
                return Types.OTHER;
        }
    }

}
