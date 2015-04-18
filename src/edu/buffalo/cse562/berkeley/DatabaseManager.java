package edu.buffalo.cse562.berkeley;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import edu.buffalo.cse562.iterator.TableIterator;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

public class DatabaseManager {
  private static final Map<String, Database> DATABASES = new ConcurrentHashMap<String, Database>();
  private static Environment                 environment;

  public static void preprocess() {
    open();

    for (String name : DATABASES.keySet()) {
      DataTable dataTable = TableManager.getTable(name);
      TableIterator iterator = new TableIterator(dataTable.getTable(), dataTable.getSchema());
      List<Integer> primaryIndexes = dataTable.getSchema().getPrimaryIndexes();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      DataOutputStream dataOut = new DataOutputStream(out);
      Database database = DATABASES.get(name);
      
      while (iterator.hasNext()) {
        Row row = iterator.next();
        
        try {
          row.writeOut(dataOut, null);
          DatabaseEntry data = new DatabaseEntry(out.toByteArray());
          out.reset();
          row.writeOut(dataOut, primaryIndexes);
          DatabaseEntry key = new DatabaseEntry(out.toByteArray());
          out.reset();
          database.put(null, key, data);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    close();
  }
  
  public static void open() {
    if (environment != null) return;
    try {
      EnvironmentConfig environmentConfig = new EnvironmentConfig();
      environmentConfig.setAllowCreate(true);
      environment = new Environment(new File(TableManager.getDbDir()), environmentConfig);
      
      for (String name : TableManager.getAllTableNames()) {
        if (DATABASES.containsKey(name)) continue;
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        DATABASES.put(name, environment.openDatabase(null, name, databaseConfig));
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }
  
  public static void close() {
    if (environment == null) return;
    for (String name : DATABASES.keySet()) {
      DATABASES.get(name).close();
      DATABASES.remove(name);
    }
    environment.close();
    environment = null;
  }
  
  /** This class may never be instantiated. */
  private DatabaseManager() {}
}
