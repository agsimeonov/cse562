package edu.buffalo.cse562.database;

import java.io.File;
import java.io.FileNotFoundException;
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
  private static File                               dataDir;

  /** This class must never be instantiated. */
  private Database() {}

  public static boolean createTable(CreateTable createTable) {
    System.out.println(createTable);
    return false;
  }

  /**
   * Sets the data directory for the database.
   * 
   * @param path - path to the data directory
   * @throws FileNotFoundException when the given directory is invalid
   */
  public static void setDataDirectory(String path) throws FileNotFoundException {
    dataDir = new File(path);
    if (!dataDir.exists()) throw new FileNotFoundException(path + " is not a valid directory!");
  }
}
