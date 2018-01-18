package eg.edu.alexu.csd.oop.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import eg.edu.alexu.csd.oop.DBparser.SQLparser;
import eg.edu.alexu.csd.oop.db.ADTs.DatabaseEntries;
import eg.edu.alexu.csd.oop.db.cs17.DatabaseImplement;
import eg.edu.alexu.csd.oop.jdbc.abstract_classes.AbstractStatement;



/**
 * Created by Bassam on 11/29/2017.
 */
public class SQLStatement extends AbstractStatement {

	public SQLparser parser;
	public DatabaseImplement db;
	private List<String> batch;
	private String dbName;
	private int timeOut;
	private DBResultSet resultSet;
	private Connection connection;
	boolean closed;
	Object[][] selected;


	final static Logger log = Logger.getLogger(SQLStatement.class.getName());

	public SQLStatement(Connection con, String databaseName) {

		connection = con;
		dbName = databaseName;
		batch = new ArrayList<String>();
		db = new DatabaseImplement();
		parser = new SQLparser();
		timeOut = 0;
		closed = false;
		selected = null;

		if (dbName != "") {
			try {
				execute("Create database " + dbName);
			} catch (SQLException e) {
				throw new RuntimeException("can't create database in statement constructor");
			}
		}

	}

	 @Override
	    public ResultSet executeQuery(final String sql) throws SQLException {

		 	log.info("Executing query ...");
	    	if (dbName.equals("")){
				 log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			 }

	    	if (this.isClosed()) {
	    		log.error("Failed to execute query : Statement is closed");
	    		throw new SQLException("Failed to execute update : Statement is closed");
	    	}

	    	if (timeOut == 0) {
	    		try {
	    			selected = db.executeQuery(sql);
				} catch (Exception e) {
					log.error(e.getMessage());
					throw new SQLException(e.getMessage());
				}
	    		log.info("Select is successful");
		    	return resultSet = new DBResultSet(this, db.getResultInRows());
	    	}

	    	ExecutorService thread = Executors.newSingleThreadExecutor();
			Future<DatabaseEntries> future = thread.submit(new Callable<DatabaseEntries>() {
				@Override
				public DatabaseEntries call() throws Exception {
					try {
			    		selected = db.executeQuery(sql);
					} catch (Exception e) {
						log.error(e.getMessage());
						throw new SQLException(e.getMessage());
					}
					return db.getResultInRows();
				}
			});

			DatabaseEntries result = null;

			    try {
					result = future.get(timeOut, TimeUnit.SECONDS);
				} catch (InterruptedException |ExecutionException e) {
				} catch (TimeoutException e) {
					log.error("Failed to execute update : "
		    				+ "\n" + sql + " timeout");
		    		throw new SQLException(sql+ "exceeded time");
				}

			    thread.shutdown();
	    	log.info("Select is successful");
	    	return resultSet = new DBResultSet(this, result);
	    }
	 public Object[][] selectedRows () {
		 return selected;
	 }
	    @Override
	    public int executeUpdate(final String sql) throws SQLException {
	    	log.info("Executing Update query ...");
	    	if (dbName.equals("")){
				 log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			 }

	    	if (this.isClosed()) {
	    		log.error("Failed to execute update : Statement is closed");
	    		throw new SQLException("Failed to execute update : Statement is closed");
	    	}



	    	if (timeOut == 0) {
	    		int result;
		    	try {
			    	result = db.executeUpdateQuery(sql);
				} catch (SQLException e) {
					log.error(e.getMessage());
					throw new SQLException(e.getMessage());
				}
		    	log.info("Table updated successfully");
		        return result;
	    	}

	    	ExecutorService thread = Executors.newSingleThreadExecutor();
			Future<Integer> future = thread.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int result;
					try {
						result  = db.executeUpdateQuery(sql);
					} catch (Exception e) {
						log.error(e.getMessage());
						throw new SQLException(e.getMessage());
					}
					return result;
				}
			});

			int result = 0;

			    try {
					result = future.get(timeOut, TimeUnit.SECONDS);
				} catch (InterruptedException |ExecutionException e) {
				} catch (TimeoutException e) {
					log.error("Failed to execute update : "
		    				+ "\n" + sql + " timeout");
		    		throw new SQLException(sql+ "exceeded time");
				}

			  thread.shutdown();

	    	log.info("Table updated successfully");
	        return result;
	    }

	    @Override
	    public void close() throws SQLException {
	    	log.info("Executing Statement Closure.... ");
	    	if (dbName.equals("")){
				 log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			}

	    	if (this.isClosed()) {
	    		log.error("Failed to close Statemet : Statement is already closed");
	    		throw new SQLException("Failed to cloase Statemet "
	    				+ ": Statement is already closed");
	    	}

			dbName = "";
			closed = true;
			batch = null;
			db = null;
			parser = null;

			timeOut = 0;

			if (resultSet != null) {
				resultSet.close();
			}

	    	log.info("Statement is closed Successfully");
	    }

	    @Override
	    public boolean isClosed() throws SQLException {
	        return closed;
	    }

	    @Override
	    public int getQueryTimeout() throws SQLException {
	    	if (dbName.equals("")) {
				 log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			}

	    	if (this.isClosed()) {
	    		log.error("Failed to get query timeOut : Statement is closed");
	    		throw new SQLException("Failed to get query timeOut : Statement is closed");
	    	}
	    	log.info("Executing Query time out");
	       return timeOut;
	    }

	    @Override
	    public void setQueryTimeout(int seconds) throws SQLException {
	    	log.info("Setting query TimeOut ..... ");
	    	if (dbName.equals("")){
				 log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			}

	    	if (this.isClosed()) {
	    		log.error("Failed to set TimeOut : "
	    			+"\n"+ "Statement is already closed");
	    		throw new SQLException("Failed to set TimeOut  : "
	    				+ "Statement is already closed");
	    	}
	    	if (dbName.equals("")){
				 log.error("Failed to add Batch : No database is Identified");
			}
	    	log.info("TimeOut is set Successful");
	    	timeOut = seconds;
	    }

	    @Override
	    public void addBatch(String sql) throws SQLException {
	    	log.info("Adding Batch .... ");
	    	if (dbName.equals("")){
				 log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			}

	    	if (this.isClosed()) {
	    		log.error("Failed to add Batch : "
	    				+"\n"+ "Statement is already closed");
	    		throw new SQLException("Failed to add Batch  : "
	    				+ "Statement is already closed");
	    	}
	    	if (dbName.equals("")){
				log.error("Failed to add Batch : No database is Identified");
			 }
    		log.info("Batch added Successfully");
	    	batch.add(sql);
	    }

	    @Override
	    public void clearBatch() throws SQLException {

	    	log.info("Clearing Batch .... ");

	    	if (dbName.equals("")){
				 log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			}

	    	if (this.isClosed()) {
	    		log.error("Failed to clear Batch :"
	    			+"\n"+ "Statement is already closed");
	    		throw new SQLException("Failed to clear Batch :"
	    				+ "Statement is already closed");
	    	}
	    	log.info("Batch is cleared Successfully");
	    	batch.clear();
	    }

	    @Override
	    public boolean execute(String sql) throws SQLException {
	    	log.info("Executing SQL Query .... ");
	    	if (this.isClosed()) {
	    		log.error("Failed to execute Query : Statement is closed");
	    		throw new SQLException("Failed to execute Query : Statement is closed");
	    	}

	    	if (dbName.equals("")){
					 log.error("No database is Identified");
					 throw new SQLException("No database is Identified");
			}


	    	parser.checkSyntax(sql);
	    	if (parser.getStatement() != null &&
	    			parser.getStatement().equalsIgnoreCase("CREATE")) {
	    		executeStructure(sql);
	    		return false;
	    	} else if (parser.getStatement() != null &&
	    			parser.getStatement().equalsIgnoreCase("SELECT")) {
	    		executeQuery(sql);
	    		return true;
	    	} else if  (parser.getStatement() != null &&
	    			parser.getStatement().equalsIgnoreCase("DROP")) {
	    		executeStructure(sql);
	    		return true;
	    	}
	    	executeUpdate(sql);
	    	return false;
	    }

	    private void executeStructure(String sql) throws SQLException {


	    	if (timeOut == 0) {
	    		try {
					db.executeStructureQuery(sql);
				} catch (SQLException e) {
					log.error(e.getMessage());
					throw new SQLException(e.getMessage());
				}
		    	log.info(sql + " Executed successfully");
		    	return;
	    	}

	    	ExecutorService thread = Executors.newSingleThreadExecutor();
			Future<Boolean> future = thread.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					Boolean result;
					try {
						result = db.executeStructureQuery(sql);
					} catch (Exception e) {
						log.error(e.getMessage());
						throw new SQLException(e.getMessage());
					}
					return result;
				}
			});

			Boolean result = false;

			    try {
					result = future.get(timeOut, TimeUnit.SECONDS);
				} catch (InterruptedException |ExecutionException e) {
				} catch (TimeoutException e) {
					log.error("Failed to execute update : "
		    				+ "\n" + sql + " timeout");
		    		throw new SQLException(sql+ "exceeded time");
				}

			  thread.shutdown();


	    	log.info(sql + " Executed successfully");

	    }

	    @Override
	    public int[] executeBatch() throws SQLException {
	    	log.info("Executing batch...");
	    	if (dbName.equals("")){
				 log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			 }

	    	if (this.isClosed()) {
	    		log.error("Failed to execute Batch : Statement is closed");
	    		throw new SQLException("Failed to execute Batch : Statement is closed");
	    	}

	    	if (batch.isEmpty()) {
	    		log.error("Failed to execute Batch : Batch is empty");
	    		throw new SQLException("Failed to execute Batch : Batch is empty");
	    	}

	    	int[] result = new int[batch.size()];
	    	for (int i = 0; i < batch.size(); i++) {
	    		if (timeOut == 0) {
	    			try {
		    			result[i] = db.executeUpdateQuery(batch.get(i));
					} catch (Exception e) {
						log.error(e.getMessage());
						throw new SQLException(e.getMessage());
					}
	    		} else {
	    			final int j = i;
	    			ExecutorService thread = Executors.newSingleThreadExecutor();
	    			Future<Integer> future = thread.submit(new Callable<Integer>() {
	    				@Override
	    				public Integer call() throws Exception {

	    					int result;
	    					try {
	    						result  = db.executeUpdateQuery(batch.get(j));
	    					} catch (Exception e) {
	    						log.error(e.getMessage());
	    						throw new SQLException(e.getMessage());
	    					}
	    					return result;
	    				}
	    			});

	    			int ans = 0;
	    			    try {
	    					ans = future.get(timeOut, TimeUnit.SECONDS);
	    				} catch (InterruptedException |ExecutionException e) {
	    				} catch (TimeoutException e) {
	    					log.error("Failed to execute update : "
	    		    				+ "\n" + batch.get(j) + " timeout");
	    		    		throw new SQLException(batch.get(j)+ "exceeded time");
	    				}

	    			  thread.shutdown();
	    			  result[i] = ans;
	    		}


	    	}

	    	batch.clear();
	    	log.info("Batch executed successfully");
	        return result;
	    }

	    @Override
	    public Connection getConnection() throws SQLException {
	    	log.info("Getting connection .... ");
	    	if (dbName.equals("")){
				log.error("No database is Identified");
				 throw new SQLException("No database is Identified");
			 }
	    	if (this.isClosed()) {
	    		log.error("Failed to get connection : Statement is already closed");
	    		throw new SQLException("Failed to get connection : Statement is already closed");
	    	}
	    	if (connection == null || connection.isClosed()) {
	    		log.error("Failed to get connection : Connection is closed");
	    		throw new SQLException("Failed to get connection : Connection is closed");
	    	}
	    	log.info("Getting Connection is Successful");
	        return connection;
	    }

}
