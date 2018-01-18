package eg.edu.alexu.csd.oop.view;

import java.io.File;
import java.sql.SQLException;
import java.util.Properties;

import eg.edu.alexu.csd.oop.DBparser.SQLparser;
import eg.edu.alexu.csd.oop.jdbc.SQLConnection;
import eg.edu.alexu.csd.oop.jdbc.SQLDriver;
import eg.edu.alexu.csd.oop.jdbc.SQLStatement;

public class Controller {
	public SQLDriver d;
	public SQLConnection c;
	public SQLStatement s;
	private String error;
	private int row = 0;


	public String execute(String sql) {

		SQLparser parser = new SQLparser();
		boolean valid = parser.checkSyntax(sql);

		if (valid && parser.getStatement().equalsIgnoreCase("SELECT")) {
			try {
				setChangedRows(0);
				s.executeQuery(sql);
			} catch (SQLException e) {
				return e.getMessage();
			}
		} else if (valid && (parser.getStatement().equalsIgnoreCase("DROP")
				|| parser.getStatement().equalsIgnoreCase("CREATE"))) {
			try {
				s.execute(sql);
			} catch (SQLException e) {
				return e.getMessage();
			}
		} else {
			try {
				setChangedRows(0);
				int result = s.executeUpdate(sql);
				setChangedRows(result);
			} catch (SQLException e) {
				return e.getMessage();
			}

		}

		return "Successful";
	}

	public void setChangedRows(int rowNumber) {
		row = rowNumber;
	}

	public int getChangedRows() {
		return row;
	}


	public String registerDriver(String path) {
		d = new SQLDriver();
		File f = new File(path);
		if (!f.isDirectory() || !f.exists()) {
			return "Database directory not found";
		}

		Properties p = new Properties();
		p.put("path", f.getAbsoluteFile());
		try {
			c = (SQLConnection) d.connect("jdbc:xmldb://localhost", p);
		} catch (SQLException e) {
			return e.getMessage();
		}
		try {
			s = (SQLStatement) c.createStatement();
		} catch (SQLException e) {
			return e.getMessage();
		}
		return "Connection is successful";
	}


	public String action(String action, String sql) {

		if (action.equalsIgnoreCase("ADD")) {
			if (sql == null || sql.equals("")) {
				return "Failed to execute : No query was entered";
			}
			try {
				s.addBatch(sql);
			} catch (SQLException e) {
				return e.getMessage();
			}
		} else if (action.equalsIgnoreCase("CLEAR")) {
			try {
				s.clearBatch();
			} catch (SQLException e) {
				return e.getMessage();
			}

		} else if (action.equalsIgnoreCase("CLOSE")) {
			try {
				s.close();
			} catch (SQLException e) {
				return e.getMessage();
			}
		} else if (action.equalsIgnoreCase("TIMEOUT")) {
			try {
				s.setQueryTimeout(Integer.parseInt(sql));
			} catch (SQLException e) {
				return e.getMessage();
			}
		}
		return "Successful";
	}

	public int[] executeBat() {
		int[] result = null;
			try {
				result = s.executeBatch();
				setMsg("Successful");
			} catch (SQLException e) {
				setMsg(e.getMessage());
			}
			return result;
	}
	private void setMsg(String msg) {
		error = msg;
	}
	public String getMsg() {
		return error;
	}

}
