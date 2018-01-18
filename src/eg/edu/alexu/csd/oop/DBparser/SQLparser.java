/**
 *
 */
package eg.edu.alexu.csd.oop.DBparser;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Personal
 */
public class SQLparser {

    /**
     * Input query.
     */
    private String SQLquery;
    private String tableName;
    private String dbName;
    private String statement;

    private ArrayList<String> condition = new ArrayList<String>();
    private ArrayList<String> columns = new ArrayList<String>();
    private ArrayList<Integer> columnsTypes = new ArrayList<Integer>();
    private ArrayList<String> columnsValues = new ArrayList<String>();

    /**
     * @return true if query syntax is correct.
     */
    public boolean checkSyntax(String query) {
        if (query.equals(null)) {
            return false;
        }
        condition.clear();
        columns.clear();
        columnsValues.clear();

        setQuery(query);
        setStatement();


        boolean correctSyntax = false;
        switch (statement) {
            case "CREATE":
                correctSyntax = checkCreate();
                break;
            case "INSERT":
                correctSyntax = checkInsert();
                break;
            case "DELETE":
                correctSyntax = checkDelete();
                break;
            case "DROP":
                correctSyntax = checkDrop();
                break;
            case "SELECT":
                correctSyntax = checkSelect();
                break;
            case "UPDATE":
                correctSyntax = checkUpdate();
                break;
            default:
                correctSyntax = false;
                break;
        }

        return correctSyntax;
    }

    /**
     * @return true if syntax is correct.
     */
    private boolean checkCreate() {
        String regex1 = "\\s*CREATE\\s+DATABASE\\s+([a-zA-Z0-9\\\\.////_-])+\\s*(;?)\\s*";
        String regex2 = "\\s*CREATE\\s+TABLE\\s+([a-zA-Z0-9._-])+"
                + "\\s*\\(\\s*([a-zA-Z0-9._-])+\\s+(VARCHAR|INT|INTEGER)\\s*(,\\s*([a-zA-Z0-9._-])+"
                + "\\s+(VARCHAR|INT)\\s*)*\\)\\s*(;?)\\s*";

        boolean pattern1Match =
                Pattern.compile(regex1).matcher(SQLquery.toUpperCase()).matches();
        boolean pattern2Match =
                Pattern.compile(regex2).matcher(SQLquery.toUpperCase()).matches();
        if (pattern1Match) {
            setDBname(findDBname());
            return true;
        } else if (pattern2Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("TABLE") + 6, SQLquery.indexOf('('));
            setTableName(findTableName(subString));
            subString = SQLquery.substring(SQLquery.indexOf('(') + 1, SQLquery.indexOf(')'));
            findCreateCol(subString);
            return true;
        }

        return false;
    }

    /**
     * @return database name in case of creating/dropping a database.
     */
    private String findDBname() {
        String temp = SQLquery.substring(SQLquery.toUpperCase().indexOf("BASE") + 5);
        return temp.replaceAll("[;| ]", "");
    }

    /**
     * @return table name in case of creating/dropping a table.
     */
    private String findTableName(String subString) {
        String temp = subString.replaceAll("[;| ]", "");
        return temp;
    }

    /**
     * @return tables columns to be edited or added.
     */
    private void findCreateCol(String subString) {
        String[] temp = subString.split(",");
        columnsTypes = new ArrayList<Integer>();
        columns = new ArrayList<String>();
        for (int i = 0; i < temp.length; i++) {
            String[] columnEntry = temp[i].trim().split(" ");
            columns.add(columnEntry[0].replaceAll("[,| ]", ""));
            if (columnEntry[1].replaceAll("[,| ]", "").equalsIgnoreCase("INT")
            		|| columnEntry[1].replaceAll("[,| ]", "").equalsIgnoreCase("INTEGER")) {
                columnsTypes.add(0);
            } else {
                columnsTypes.add(1);
            }
        }
    }

    /**
     * @return true if syntax is correct.
     */
    private boolean checkInsert() {
        String regex1 = "\\s*INSERT\\s+INTO\\s+([a-zA-Z0-9._-])+\\s+VALUES\\s*"
                + "\\((\\s*([a-zA-Z0-9'._-])+\\s*\\,\\s*)*\\s*([a-zA-Z0-9'._-])+"
                + "\\s*\\)\\s*(;?)\\s*";
        boolean pattern1Match =
                Pattern.compile(regex1).matcher(SQLquery.toUpperCase()).matches();

        String regex2 = "\\s*INSERT\\s+INTO\\s+([a-zA-Z0-9._-])+\\s*"
                + "\\((\\s*([a-zA-Z0-9._-])+\\s*\\,\\s*)*"
                + "\\s*([a-zA-Z0-9._-])+\\s*\\)\\s*"
                + "VALUES\\s*\\((\\s*([a-zA-Z0-9'._-])+\\s*\\,\\s*)*"
                + "\\s*([a-zA-Z0-9'._-])+\\s*\\)\\s*(;?)\\s*";
        boolean pattern2Match =
                Pattern.compile(regex2).matcher(SQLquery.toUpperCase()).matches();

        if (pattern1Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("INTO") + 5,
                    SQLquery.toUpperCase().indexOf("VALUES"));
            setTableName(findTableName(subString));
            subString = SQLquery.substring(SQLquery.indexOf('(') + 1, SQLquery.indexOf(')'));
            findInsertVal(subString);
            return true;
        } else if (pattern2Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("INTO") + 5,
                    SQLquery.indexOf("("));
            setTableName(findTableName(subString));
            subString = SQLquery.substring(SQLquery.indexOf('(') + 1, SQLquery.indexOf(')'));
            findInsertCol(subString);
            subString = SQLquery.substring(SQLquery.indexOf(')') + 1);
            subString = subString.substring(subString.indexOf('(') + 1, subString.indexOf(')') + 1);
            findInsertVal(subString);
            return true;
        }
        return false;
    }

    private void findInsertCol(String subString) {
        columns = new ArrayList<String>();
        String[] temp = subString.split(",");
        for (int i = 0; i < temp.length; i++) {
            columns.add(temp[i].replaceAll("[,|;|\\'|)| ]", ""));
        }
    }

    private void findInsertVal(String subString) {
        columnsValues = new ArrayList<String>();
        String[] temp = subString.split(",");
        for (int i = 0; i < temp.length; i++) {
            columnsValues.add(temp[i].replaceAll("[,|;|\\'|)| ]", ""));
        }
    }

    /**
     * @return true if syntax is correct.
     */
    private boolean checkDelete() {
        String regex1 = "\\s*DELETE\\s*(\\*| )\\s*FROM\\s+([a-zA-Z0-9._-])+\\s*(;?)\\s*";
        String regex2 = "\\s*DELETE\\s+([a-zA-Z0-9._-])+\\s*"
                + "(,\\s*([a-zA-Z0-9._-])+\\s*)*\\s+FROM"
                + "\\s+([a-zA-Z0-9._-])+\\s*(;?)\\s*";
        String regex3 = "\\s*DELETE\\s+([a-zA-Z0-9._-])+"
                + "\\s*(,\\s*([a-zA-Z0-9._-])+\\s*)*\\s+FROM"
                + "\\s+([a-zA-Z0-9._-])+\\s+WHERE\\s+([a-zA-Z0-9._-])+"
                + "\\s*(=|>|<)\\s*([a-zA-Z0-9'._-])+\\s*(;?)\\s*";
        String regex4 = "\\s*DELETE\\s*(\\*| )\\s*FROM\\s+([a-zA-Z0-9._-])+"
                + "\\s+WHERE\\s+([a-zA-Z0-9._-])+"
                + "\\s*(=|>|<)\\s*([a-zA-Z0-9'._-])+\\s*(;?)\\s*";


        boolean pattern1Match =
                Pattern.compile(regex1).matcher(SQLquery.toUpperCase()).matches();
        boolean pattern2Match =
                Pattern.compile(regex2).matcher(SQLquery.toUpperCase()).matches();
        boolean pattern3Match =
                Pattern.compile(regex3).matcher(SQLquery.toUpperCase()).matches();
        boolean pattern4Match =
                Pattern.compile(regex4).matcher(SQLquery.toUpperCase()).matches();

        if (pattern1Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("FROM") + 5);
            setTableName(findTableName(subString));
            return true;
        } else if (pattern2Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("FROM") + 5);
            setTableName(findTableName(subString));

            subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("DELETE") + 7,
                    SQLquery.toUpperCase().indexOf("FROM") - 1);
            findInsertCol(subString);
            return true;

        } else if (pattern3Match || pattern4Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("FROM") + 5,
                    SQLquery.toUpperCase().indexOf("WHERE") - 1);
            setTableName(findTableName(subString));

            if (pattern4Match) {
                subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("WHERE") + 6);
                findCondition(subString);
                return true;
            }
            subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("DELETE") + 7,
                    SQLquery.toUpperCase().indexOf("FROM") - 1);
            findInsertCol(subString);

            subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("WHERE") + 6);
            findCondition(subString);
            return true;
        }

        return false;
    }

    /**
     * @return true if syntax is correct.
     */
    private boolean checkDrop() {
        String regex1 = "\\s*DROP\\s+DATABASE\\s+([a-zA-Z0-9._-])+\\s*(;?)\\s*";
        boolean pattern1Match =
                Pattern.compile(regex1).matcher(SQLquery.toUpperCase()).matches();
        String regex2 = "\\s*DROP\\s+TABLE\\s+([a-zA-Z0-9._-])+\\s*(;?)\\s*";
        boolean pattern2Match =
                Pattern.compile(regex2).matcher(SQLquery.toUpperCase()).matches();
        if (pattern1Match) {
            setDBname(findDBname());
            return true;
        } else if (pattern2Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("TABLE") + 6);
            setTableName(findTableName(subString));
            return true;
        }

        return false;
    }

    /**
     * @return true if syntax is correct.
     */
    private boolean checkSelect() {
        String regex1 = "\\s*SELECT\\s*\\*\\s*FROM\\s+\\S+\\s*(;?)\\s*";
        String regex2 = "\\s*SELECT\\s+([a-zA-Z0-9._-])+(\\s*,\\s*([a-zA-Z0-9._-])+\\s*)*"
                + "\\s+FROM\\s+([a-zA-Z0-9._-])+\\s*(;?)\\s*";
        String regex3 = "\\s*SELECT\\s+\\S+(\\s*,\\s*([a-zA-Z0-9._-])+\\s*)*"
                + "\\s+FROM\\s+([a-zA-Z0-9._-])+\\s+WHERE\\s+([a-zA-Z0-9._-])+"
                + "\\s*(=|<|>)\\s*([a-zA-Z0-9'._-])+\\s*(;?)\\s*";
        String regex4 = "\\s*SELECT\\s*(\\*| )\\s*FROM\\s+([a-zA-Z0-9._-])+"
                + "\\s+WHERE\\s+([a-zA-Z0-9._-])+"
                + "\\s*(=|>|<)\\s*([a-zA-Z0-9'._-])+\\s*(;?)\\s*";


        boolean pattern1Match =
                Pattern.compile(regex1).matcher(SQLquery.toUpperCase()).matches();
        boolean pattern2Match =
                Pattern.compile(regex2).matcher(SQLquery.toUpperCase()).matches();
        boolean pattern3Match =
                Pattern.compile(regex3).matcher(SQLquery.toUpperCase()).matches();
        boolean pattern4Match =
                Pattern.compile(regex4).matcher(SQLquery.toUpperCase()).matches();

        if (pattern1Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("FROM") + 5);
            setTableName(findTableName(subString));
            return true;
        } else if (pattern2Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("FROM") + 5);
            setTableName(findTableName(subString));

            subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("SELECT") + 7,
                    SQLquery.toUpperCase().indexOf("FROM") - 1);
            findInsertCol(subString);
            return true;
        } else if (pattern3Match | pattern4Match) {
            String subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("FROM") + 5,
                    SQLquery.toUpperCase().indexOf("WHERE") - 1);
            setTableName(findTableName(subString));

            if (pattern4Match) {
                subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("WHERE") + 6);
                findCondition(subString);
                return true;

            }
            subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("SELECT") + 7,
                    SQLquery.toUpperCase().indexOf("FROM") - 1);
            findInsertCol(subString);

            subString = SQLquery.substring(SQLquery.toUpperCase().indexOf("WHERE") + 6);
            findCondition(subString);
            return true;
        }
        return false;
    }

    /**
     * @return true if syntax is correct.
     */
    private boolean checkUpdate() {
        String regex1 = "\\s*UPDATE\\s+([a-zA-Z0-9._-])+\\s+SET\\s+"
                + "(([a-zA-Z0-9._-])+\\s*=\\s*([a-zA-Z0-9'._-])+\\s*,\\s*)*"
                + "([a-zA-Z0-9._-])+\\s*=\\s*([a-zA-Z0-9'._-])+\\s*(;?)\\s*";
        String regex2 = "\\s*UPDATE\\s+([a-zA-Z0-9._-])+\\s+SET\\s+"
                + "(([a-zA-Z0-9._-])+\\s*=\\s*([a-zA-Z0-9'._-])+\\s*,\\s*)*"
                + "([a-zA-Z0-9._-])+\\s*=\\s*"
                + "([a-zA-Z0-9'._-])+\\s+WHERE\\s+([a-zA-Z0-9._-])+\\s*"
                + "(=|>|<)\\s*([a-zA-Z0-9'._-])+\\s*(;?)\\s*";

        boolean pattern1Match =
                Pattern.compile(regex1).matcher(SQLquery.toUpperCase()).matches();
        boolean pattern2Match =
                Pattern.compile(regex2).matcher(SQLquery.toUpperCase()).matches();

        if (pattern1Match || pattern2Match) {
            int firstIndex = SQLquery.toUpperCase().indexOf("UPDATE") + 7;
            int lastIndex = SQLquery.toUpperCase().indexOf("SET") - 1;
            String subString = SQLquery.substring(firstIndex, lastIndex);
            setTableName(findTableName(subString));

            firstIndex = lastIndex + 5;
            if (pattern1Match) {
                findUpdateCol(SQLquery.substring(firstIndex));
            } else {
                lastIndex = SQLquery.toUpperCase().indexOf("WHERE") - 1;
                findUpdateCol(SQLquery.substring(firstIndex, lastIndex));

                firstIndex = lastIndex + 7;
                findCondition(SQLquery.substring(firstIndex));
            }
            return true;
        }
        return false;
    }

    private void findUpdateCol(String subString) {
        ///low da5ly ID = '1' el mfroud 3'lt bs 7ayshta3'l 3ady.
        ///ana basheel ay spaces f-ay name = 'njr nrj' 7ytshal meno el spaces.
        String[] temp = subString.split(",");
        columnsValues = new ArrayList<String>();
        columns = new ArrayList<String>();
        for (int i = 0; i < temp.length; i++) {
            String[] colValue = temp[i].split("=");
            columns.add(colValue[0].replaceAll("[,|;| ]", ""));
            columnsValues.add(colValue[1].replaceAll("[,|;|\\'| ]", ""));
        }
    }

    private void findCondition(String subString) {
        condition = new ArrayList<String>();
        String[] temp = subString.split("=|>|<");

        condition.add(temp[0].replaceAll("[,|;|\\'| ]", ""));

        int index1 = subString.indexOf('=');
        int index2 = subString.indexOf('>');
        int index3 = subString.indexOf('<');
        if (index1 > 0) {
            condition.add("=");
        } else if (index2 > 0) {
            condition.add(">");
        } else {
            condition.add("<");
        }
        condition.add(temp[1].replaceAll("[,|;|\\'| ]", ""));
    }

    /**
     * sets query.
     */
    private void setQuery(String query) {
        SQLquery = query.replaceAll("\n", " ").trim().replaceAll(" +", " ");
    }

    /**
     * @return query of user.
     */
    public String getTableName() {
        return tableName;
    }

    private void setTableName(String name) {
        tableName = name;
    }

    public String getDBname() {
        return dbName;
    }

    private void setDBname(String name) {
        dbName = name;
    }

    public ArrayList<String> getColumnsNames() {
        return columns;
    }

    public ArrayList<String> getColumnsValues() {
        return columnsValues;
    }

    public ArrayList<String> getCondition() {
        return condition;
    }

    public ArrayList<Integer> getColumnsTypes() {
        return columnsTypes;
    }

    private void setStatement() {
        StringBuffer st = new StringBuffer();
        for (int i = 0; i < SQLquery.length(); i++) {
            if ((SQLquery.charAt(i) == ' ' || SQLquery.charAt(i) == '*')
                    && i != 0 && Character.isLetter(SQLquery.charAt(i - 1))) {
                break;
            }
            st.append(SQLquery.charAt(i));
        }
        statement = st.toString().replaceAll(" ", "").toUpperCase();
    }

    public String getStatement() {
        return statement;
    }
}

