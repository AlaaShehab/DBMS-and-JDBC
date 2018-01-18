package eg.edu.alexu.csd.oop.db.ADTs;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bassam on 11/18/2017.
 */
public class DatabaseColumn {

    private String columnName;
    private int columnDataType;
    private List<DatabaseElement> columnElements;

    public DatabaseColumn(String columnName, int columnDataType) {
        this.columnName = columnName;
        this.columnDataType = columnDataType;
        columnElements = new ArrayList<DatabaseElement>();
    }

    public boolean addElement(DatabaseElement databaseElement) {
        if (isSupportedDataType(databaseElement)) {
            columnElements.add(databaseElement);
            return true;
        } else {
            try {
                throw new DatatypeConfigurationException("Not supported data type");
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    ///added by Alaa
    public void deleteElementAt(int index) {
        columnElements.remove(index);
    }

    public boolean deleteElement(DatabaseElement databaseElement) {

        int dataType = databaseElement.getDataType();
        Object value = databaseElement.getValue();

        for (int i = 0; i < columnElements.size(); i++) {
            DatabaseElement element = columnElements.get(i);
            if (element.getDataType() == dataType && element.getValue().equals(value)) {
                columnElements.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean updateElement(DatabaseElement oldElement, DatabaseElement newElement) {

        int oldElementDataType = oldElement.getDataType();
        Object oldElementValue = oldElement.getValue();

        if (oldElementDataType != newElement.getDataType()) {
            return false;
        }

        for (int i = 0; i < columnElements.size(); i++) {
            DatabaseElement element = columnElements.get(i);
            if (element.getDataType() == oldElementDataType && element.getValue().equals(oldElementValue)) {
                columnElements.set(i, newElement);
                return true;
            }
        }
        return false;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnDataType() {
        return columnDataType;
    }

    public List<DatabaseElement> getColumnElements() {
        return columnElements;
    }

    public void setColumnElements(List<DatabaseElement> columnElements) {
        this.columnElements = columnElements;
    }

    public boolean equals(DatabaseColumn databaseColumn) {
        return columnName.equalsIgnoreCase(databaseColumn.getColumnName()) &&
                columnDataType == databaseColumn.getColumnDataType() &&
                columnElements.size() == databaseColumn.getColumnElements().size();
    }

    private boolean isSupportedDataType(DatabaseElement databaseElement) {
        int dataType = databaseElement.getDataType();
        return dataType == DatabaseElement.DATATYPE_INT || dataType == DatabaseElement.DATATYPE_VARCHAR;
    }

    ///added by Alaa
    public void deleteAllEntries() {
        columnElements.clear();
    }

    ///added by Alaa
    public void setAllEntriesNull() {
        int size = columnElements.size();
        columnElements.clear();
        for (int i = 0; i < size; i++) {
            columnElements.add(null);
        }
    }

    ////added by Alaa
    public boolean addElementAt(int index, DatabaseElement databaseElement) {
        if (isSupportedDataType(databaseElement)) {
            columnElements.add(index, databaseElement);
            return true;
        } else {
            try {
                throw new DatatypeConfigurationException("Not supported data type");
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

/*
    public void addElements(List<DatabaseElement> databaseElements) {

        if (isSupportedDataType(databaseElements.get(0))) {
            columnElements.addAll(databaseElements);

        } else {
            try {
                throw new DatatypeConfigurationException("Not supported data type");
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
    }*/
}
