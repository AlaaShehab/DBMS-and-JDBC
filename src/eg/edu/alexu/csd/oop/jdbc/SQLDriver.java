package eg.edu.alexu.csd.oop.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * Created by Bassam on 11/29/2017.
 */
public class SQLDriver implements Driver {

	DriverManager connectionManager;
	String validurl;
	Properties x;

	final static Logger log = Logger.getLogger(SQLDriver.class.getName());

	public SQLDriver(){
		this.validurl = "jdbc:xmldb://localhost";
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		log.info("Checking URL for validity....");
		if(url.equals(validurl)) {
			log.info("URL is valid");
			return true;
		}
		log.error("Failed to accept URL : URL is incorrect");
		return false;
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {

		log.info("Connecting to database .... ");
		File dir = (File) info.get("path");
	    String path = dir.getAbsolutePath();

	    log.info("Connection is successful");
	    return new SQLConnection(path);
	}


	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		log.info("Getting Driver property info ... ");
	    Driver d = DriverManager.getDriver(url);

	    if (d != null) {
	    	log.info("Porperty info is sent successfully");
	    	return d.getPropertyInfo(url, info);
	    } else {
	    	log.error("Porperty info failed to send");
			return null;
	    }
	}

	//unused

	@Override
	public int getMajorVersion() {
		throw new UnsupportedOperationException("Not Supported Feature");

	}

	@Override
	public int getMinorVersion() {
		throw new UnsupportedOperationException("Not Supported Feature");

	}

	@Override
	public boolean jdbcCompliant() {
		throw new UnsupportedOperationException("Not Supported Feature");

	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException("Not Supported Feature");

	}
}
