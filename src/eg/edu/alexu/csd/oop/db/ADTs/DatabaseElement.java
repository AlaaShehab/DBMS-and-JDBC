package eg.edu.alexu.csd.oop.db.ADTs;

/**
 * Created by Bassam on 11/18/2017.
 */
public class DatabaseElement<T> {

    public static final int DATATYPE_INT = 0;
    public static final int DATATYPE_VARCHAR = 1;

    private T value;
    private int dataType;

    public DatabaseElement(T value, int dataType) {
        this.value = value;
        this.dataType = dataType;
    }

    public int getDataType() {
        return dataType;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        return value.equals(obj);
    }
}
