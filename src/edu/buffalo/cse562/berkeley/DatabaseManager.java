package edu.buffalo.cse562.berkeley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.TableManager;

public class DatabaseManager {
  private static final Map<String, Database> DATABASES = new ConcurrentHashMap<String, Database>();
  private static Environment                 environment;

  public static void preprocess() {
    open();

    for (String name : DATABASES.keySet()) {
      DataTable dataTable = TableManager.getTable(name);
      dataTable.getDataFile();

      try {
        BufferedReader reader = new BufferedReader(new FileReader(dataTable.getDataFile()));
        while (reader.ready()) {
          String[] data = reader.readLine().split("\\|");
          for (int i = 0; i < dataTable.getSchema().size(); i++) {
            switch (dataTable.getTypes().get(i).toLowerCase()) {
              case "int":
                break;
              case "decimal":
                break;
              case "date":
                break;
              default:
                // string
                break;
            }
          }
        }
        reader.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
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
