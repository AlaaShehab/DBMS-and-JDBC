package eg.edu.alexu.csd.oop.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import eg.edu.alexu.csd.oop.jdbc.abstract_classes.AbstractConnection;

/**
 * Created by Bassam on 11/29/2017.
 */
public class SQLConnection extends AbstractConnection {


	SQLStatement statement;
	String databaseName = "";
	boolean closed;

	final static Logger log = Logger.getLogger(SQLConnection.class.getName());

	public SQLConnection (String path) {

		if (path == null || path == "") {
			databaseName ="";
		} else {
			databaseName = getName(path);
		}

		closed = false;
	}

	@Override
	public Statement createStatement() throws SQLException {
		log.info("Creating Statement ...");
		if (this.isClosed()) {
			log.error("Failed to create statement "
    				+ ": Connection is already closed");
			throw new SQLException("Failed to create statement "
    				+ ": Connection is already closed");
		}
		log.info("Statement is created successfully");
		return new SQLStatement(this, databaseName);
	}

	@Override
	public void close() throws SQLException {
		if (this.isClosed()) {
			log.error("Failed to close connection "
    				+ ": Connection is already closed");
			throw new SQLException("Failed to close Connection "
    				+ ": Connection is already closed");
		}
		closed = true;

		if (statement != null) {
			try {
				statement.close();
			} catch (Exception e) {
			}
		}

		databaseName = "";
		log.info("Connection is closed successfully");

	}

	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}

	private String getName (String path) {
		String[] temp = null;
		if (path.contains("/")) {
			temp = path.split("/");
		} else {
			temp = path.split("\\\\");
		}
		return temp[temp.length - 1];
	}

}
