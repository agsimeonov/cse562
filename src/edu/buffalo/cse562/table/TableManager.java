package edu.buffalo.cse562.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.AbstractMap;
import java.util.HashMap;

import net.sf.jsqlparser.statement.create.table.CreateTable;

/**
 * An abstraction used to manage tables and their respective data files.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public final class TableManager {
  private static final AbstractMap<String, DataTable> DB_TABLES = new HashMap<String, DataTable>();
  private static String                               dataDir;
  private static String                               swapDir;

  /**
   * Creates a new database table.
   * 
   * @param createTable creates a new database table
   * @return true if a new table was created, otherwise false
   */
  public static boolean createTable(CreateTable createTable) {
    if (DB_TABLES.containsKey(createTable.getTable().getName().toLowerCase())) {
      return false;
    } else {
      try {
        DataTable table = new DataTable(createTable);
        DB_TABLES.put(createTable.getTable().getName().toLowerCase(), table);
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
   * Acquires the swap directory path for the database.
   * 
   * @return the swap directory path for the database.
   */
  public static String getSwapDir() {
    return swapDir;
  }
  
  /**
   * Acquires a database table.
   * 
   * @param name - desired table name
   * @return desired table
   */
  public static DataTable getTable(String name) {
    return DB_TABLES.get(name.toLowerCase());
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
    setDir(path);
  }
  
  /**
   * Sets (and creates if necessary) a valid swap directory path for the database.
   * 
   * @param path - path to the swap directory
   * @throws IOException when the given directory could not be created
   * @throws NotDirectoryException when the given path is not a valid directory
   */
  public static void setSwapDir(String path) throws IOException, NotDirectoryException {
    swapDir = path;
    setDir(path);
  }
  
  /**
   * Makes sure a given directory path exists.
   * 
   * @param path - a given directory path
   * @throws IOException when the given directory could not be created
   * @throws NotDirectoryException when the given path is not a valid directory
   */
  private static void setDir(String path) throws IOException, NotDirectoryException {
    File dir = new File(path);

    if (!dir.exists()) {
      if (!dir.mkdirs()) throw new IOException(path + " directory could not be created!");
    }

    if (!dir.isDirectory()) throw new NotDirectoryException(path + "is not a valid directory");
  }

  /** This class may never be instantiated. */
  private TableManager() {}
}
