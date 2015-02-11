package edu.buffalo.cse562.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.AbstractMap;
import java.util.HashMap;

import net.sf.jsqlparser.statement.create.table.CreateTable;

/**
 * An abstraction used to manage tables in the database.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public final class Database {
  private static final AbstractMap<String, DbTable> DB_TABLES = new HashMap<String, DbTable>();
  private static String                             dataDir;

  /**
   * Creates a new database table.
   * 
   * @param createTable creates a new database table
   * @return true if a new table was created, otherwise false
   */
  public static boolean createTable(CreateTable createTable) {
    if (DB_TABLES.containsKey(createTable.getTable())) {
      return false;
    } else {
      try {
        DbTable table = new DbTable(createTable);
        DB_TABLES.put(createTable.getTable().getName(), table);
        return true;
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }
  }

  /**
   * Acquires the data directory path for the database.
   * 
   * @return the data directory path for the database.
   */
  public static String getDataDir() {
    return dataDir;
  }
  
  /**
   * Acquires a database table.
   * 
   * @param name - desired table name
   * @return desired table
   */
  public static DbTable getTable(String name) {
    return DB_TABLES.get(name);
  }

  /**
   * Sets (and creates if necessary) a valid data directory path for the database.
   * 
   * @param path - path to the data directory
   * @throws IOException when the given directory could not be created
   * @throws NotDirectoryException when the given path is not a valid directory
   */
  public static void setDataDir(String path) throws IOException, NotDirectoryException {
    dataDir = path;
    File dir = new File(path);

    if (!dir.exists()) {
      if (!dir.mkdirs()) throw new IOException(path + " directory could not be created!");
    }

    if (!dir.isDirectory()) throw new NotDirectoryException(path + "is not a valid directory");
  }

  /** This class may never be instantiated. */
  private Database() {}
}
