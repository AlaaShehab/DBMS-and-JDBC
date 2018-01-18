package eg.edu.alexu.csd.oop.db.ADTs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class DbhandlerImp {


	public boolean mainDirectoryExist = false;
	public String path = System.getProperty("user.dir");

	public DbhandlerImp(){
		if(this.databaseExists("Database")){
			path = path + System.getProperty("file.separator") + "Database" + System.getProperty("file.separator");
		}
	}

    public String saveDB(DatabaseManager dataBase) {
    	if (dataBase == null) {
    	 	return null;
    	}
		if(!mainDirectoryExist){
			File file = new File("Database");
			file.mkdirs();
			path = file.getAbsolutePath() + System.getProperty("file.separator");
			mainDirectoryExist = true;
		}
        new File(path + dataBase.getDatabaseName()).mkdirs();
        for (DatabaseTable dbTable : dataBase.getAllDatabaseTables()) {
            try {
                XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
                FileOutputStream fileOutputStream = new FileOutputStream(path + dataBase.getDatabaseName() + System.getProperty("file.separator") + dbTable.getTableName() + ".xml");
                XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(fileOutputStream, "ISO-8859-1");


                xMLStreamWriter.writeStartDocument();
                xMLStreamWriter.writeStartElement("table");
                xMLStreamWriter.writeAttribute("tableName", dbTable.getTableName());

                for (DatabaseColumn dbColumn : dbTable.getTableColumns()) {
                    xMLStreamWriter.writeStartElement("column");
                    xMLStreamWriter.writeAttribute("columnName", dbColumn.getColumnName() + "");
                    xMLStreamWriter.writeAttribute("columnType", dbColumn.getColumnDataType() + "");

                    for (DatabaseElement<?> dbElement : dbColumn.getColumnElements()) {
                        xMLStreamWriter.writeStartElement("Element");
                        xMLStreamWriter.writeCharacters(dbElement.toString());
                        xMLStreamWriter.writeEndElement();
                    }
                    xMLStreamWriter.writeEndElement();
                }
                xMLStreamWriter.writeEndDocument();
                xMLStreamWriter.flush();
                xMLStreamWriter.close();
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (XMLStreamException e) {
                e.printStackTrace();
            } catch (IOException e) {
				e.printStackTrace();
			}
        }
		return null;
    }

    public DatabaseTable loadTable(String tableName, String databaseName) {
    	if (tableName.equals("") || databaseName.equals("") || tableName == null || databaseName == null || !path.contains("Database")) {
    		return null;
    	}
        DatabaseTable dbTable = null;
        DatabaseColumn dbColumn = null;
        DatabaseElement dbElement = null;
        Integer columnType;

        try {
        	String tablePath = path + System.getProperty("file.separator") + databaseName + System.getProperty("file.separator") + tableName;
        	if (!tablePath.contains("xml")) {
        		tablePath = tablePath + ".xml";
        	}
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader eventReader =
                    factory.createXMLEventReader(new FileReader(tablePath));

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                switch (event.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:

                        StartElement startElement = event.asStartElement();
                        String qName = startElement.getName().getLocalPart();
                        if (qName.equalsIgnoreCase("column")) {
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            columnType = Integer.parseInt(attributes.next().getValue());
                            String columnName = attributes.next().getValue();
                            dbColumn = new DatabaseColumn(columnName, columnType);

                        } else if (qName.equalsIgnoreCase("table")) {
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            String TableName = attributes.next().getValue();
                            dbTable = new DatabaseTable(TableName);
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = event.asCharacters();
                        if (characters.getData().contains("\n")) {
                            break;
                        }
                        dbElement = new DatabaseElement(characters.getData(), dbColumn.getColumnDataType());
                        dbColumn.addElement(dbElement);
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        EndElement endElement = event.asEndElement();
                        if (endElement.getName().getLocalPart().equalsIgnoreCase("column")) {
                            dbTable.addColumn(dbColumn);
                        }
                        break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		}

        return dbTable;
    }

    public String createDTD(DatabaseTable dbtable) {
        File file = new File(dbtable.getTableName() + ".xml");
        try {
        	FileWriter writer = new FileWriter(file);
			writer.write("<!DOCTYPE " + dbtable.getTableName().toUpperCase() + "[\n");
			writer.write("	<!ELEMENT " + dbtable.getTableName().toUpperCase() + " (COLUMN+)>\n");
			writer.write("	<!ELEMENT COLUMN (ELEMENT+)>\n");
			writer.write("	<!ELEMENT ELEMENT ANY>\n");
        	writer.write("	<!ATTLIST COLUMN COLUMNNAME CDATA #REQUIRED>\n");
        	writer.write("	<!ATTLIST COLUMN COLUMNTYPE CDATA #FIXED>\n");
        	writer.write("]>");
        	writer.flush();
        	writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

    private boolean deleteDir(File dir) {
	      if (dir.isDirectory()) {
	         String[] children = dir.list();
	         for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir (new File(dir, children[i]));
	            if (!success) {
	               return false;
	            }
	         }
	      }
	      boolean x = dir.delete();
	      return x;
	   }

	public boolean saveTable(DatabaseTable table) {
		if (table == null) {
			return false;
		}
		File file = new File(this.getPath(table.getDatabaseName()) + System.getProperty("file.separator") + table.getTableName() + ".xml");
		try {
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(new FileOutputStream(file), "ISO-8859-1");
            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("table");
            xMLStreamWriter.writeAttribute("tableName", "class");

            for (DatabaseColumn dbColumn : table.getTableColumns()) {
                xMLStreamWriter.writeStartElement("column");
                xMLStreamWriter.writeAttribute("columnName", dbColumn.getColumnName() + "");
                xMLStreamWriter.writeAttribute("columnType", dbColumn.getColumnDataType() + "");

                for (DatabaseElement<?> dbElement : dbColumn.getColumnElements()) {
                    xMLStreamWriter.writeStartElement("Element");
                    xMLStreamWriter.writeCharacters(dbElement.toString());
                    xMLStreamWriter.writeEndElement();
                }
                xMLStreamWriter.writeEndElement();
            }
            xMLStreamWriter.writeEndDocument();
            xMLStreamWriter.flush();
            xMLStreamWriter.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean databaseExists(String databaseName) {
		if (databaseName.equals("") || databaseName == null) {
			return false;
		}
		File file = new File(path);
		String[] dbNames = file.list(new FilenameFilter() {
		  @Override
		public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		for (String s : dbNames) {
			if (s.equalsIgnoreCase(databaseName)){
			    return true;
			}
		}
		return false;
	}

	public String getPath(String databaseName) {
		if (!path.contains("Database")) {
			return null;
		}
		return path + System.getProperty("file.separator") + databaseName;
	}

	public DatabaseManager loadDB(String databaseName) {
		if (databaseName == null || databaseName.equals("") || !path.contains("Database")) {
			return null;
		}
		DatabaseManager currentDB = new DatabaseManager(databaseName);
		if(databaseExists(databaseName)){
			File file = new File(getPath(databaseName));
			String[] tableNames = file.list(new FilenameFilter() {
			  @Override
			public boolean accept(File current, String name) {
			    return new File(current, name).isFile();
			  }
			});
			for(String table : tableNames) {
				if (table.contains("xml")) {
					currentDB.addTable(loadTable(table, databaseName));
				}
			}
			return currentDB;
		}
		return null;
	}

	public boolean deleteDB(String databaseName) {
		if (databaseName.equals("") || databaseName == null || !path.contains("Database")) {
			return false;
		}
		File file = new File(path);
		String[] dbNames = file.list(new FilenameFilter() {
		  @Override
		public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		for (String s : dbNames) {
			if (s.equalsIgnoreCase(databaseName)){
				File currentFile = new File(path,s);
			    deleteDir(currentFile);
			    return true;
			}
		}
		return false;
	}

	public boolean deleteTable(String tableName, String databaseName) {
		if (tableName.equals("") || databaseName.equals("") || tableName == null || databaseName == null || !path.contains("Database")) {
    		return false;
    	}
		File file = new File(path + System.getProperty("file.separator") + databaseName);
		String[] dbTables = file.list(new FilenameFilter() {
		  @Override
		public boolean accept(File current, String name) {
		    return new File(current, name).isFile();
		  }
		});
		for (String s: dbTables) {
			if (s.equalsIgnoreCase(tableName + ".xml")) {
				File currentFile = new File (getPath(databaseName) +System.getProperty("file.separator") + tableName + ".xml");
				currentFile.delete();
				return true;
			}
		}
		return false;
	}
}