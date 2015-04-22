package edu.buffalo.cse562.berkeley;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jsqlparser.schema.Column;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;

import edu.buffalo.cse562.iterator.TableIterator;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;
import edu.buffalo.cse562.table.TableManager;

public class DatabaseManager {
  private static final Map<String, Database> DATABASES = new ConcurrentHashMap<String, Database>();
  private static Environment                 environment;
  private static SecondaryDatabase secondaryDatabase = null;


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
   // System.out.println("Sec Count " + secondaryDatabase.count());

    close();
  }
  

  
  
  
  
  
  
  
  public static Database getDatabase(String name) {
    return DATABASES.get(name.toLowerCase());
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
     
        if (name.equals("r")) { 
          Schema schema = TableManager.getTable(name).getSchema();
          ArrayList<Column> columnList = schema.getColumns();
          for (Column c : columnList) { 
            if(c.getColumnName().equals("B")) { 
              int columnIndex = schema.getIndex(c);
              SecondaryConfig secondaryConfig = new SecondaryConfig();
              secondaryConfig.setAllowCreate(true);
              secondaryConfig.setKeyCreator(new KeyCreator(columnIndex));
              secondaryDatabase = environment.openSecondaryDatabase(null, name, DATABASES.get(name),secondaryConfig);
            }
          }
        }
        
        
      }
      
      
      
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }
  
  public static void close() {
    if (environment == null) return;
    if (secondaryDatabase != null) {
      secondaryDatabase.close();
    }
    
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
