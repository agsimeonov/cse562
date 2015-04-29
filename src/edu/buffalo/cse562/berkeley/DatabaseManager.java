package edu.buffalo.cse562.berkeley;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;

import edu.buffalo.cse562.iterator.TableIterator;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;
import edu.buffalo.cse562.table.TableManager;

public class DatabaseManager {
  private static final Map<String, DbUnit> DATABASES = new ConcurrentHashMap<String, DbUnit>();
  private static Environment                 environment;

  public static void preprocess() {
    open();

    for (String name : DATABASES.keySet()) {
      DataTable dataTable = TableManager.getTable(name);
      TableIterator iterator = new TableIterator(dataTable.getTable(), dataTable.getSchema());
      List<Integer> primaryIndexes = dataTable.getSchema().getPrimaryIndexes();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      DataOutputStream dataOut = new DataOutputStream(out);
      Database database = DatabaseManager.getDatabase(name);
      
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
  
  public static Database getDatabase(String name) {
    return DATABASES.get(name.toLowerCase()).primary;
  }
  
  public static void open() {
    if (environment != null) return;
    try {
      EnvironmentConfig environmentConfig = new EnvironmentConfig();
      environmentConfig.setAllowCreate(true);
      environmentConfig.setLocking(false);
      environmentConfig.setConfigParam(EnvironmentConfig.ENV_RUN_CLEANER, "false");
      environmentConfig.setConfigParam(EnvironmentConfig.ENV_RUN_CHECKPOINTER, "false");
      
      environment = new Environment(new File(TableManager.getDbDir()), environmentConfig);
      
      for (String name : TableManager.getAllTableNames()) {
        if (DATABASES.containsKey(name)) continue;
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        if (!TableManager.getLoad()) databaseConfig.setReadOnly(true);
        Database primary = environment.openDatabase(null, name, databaseConfig);
        
        DataTable dataTable = TableManager.getTable(name);
        Schema schema = dataTable.getSchema();
        List<Integer> secondaryIndexes = schema.getSecondaryIndexes();
        List<SecondaryDatabase> secondary = new ArrayList<SecondaryDatabase>();
        List<Column> columns = new ArrayList<Column>();
        for (Integer i : secondaryIndexes) {
          SecondaryConfig secondaryConfig = new SecondaryConfig();
          secondaryConfig.setAllowCreate(true);
          secondaryConfig.setSortedDuplicates(true);
          if (!TableManager.getLoad()) secondaryConfig.setReadOnly(true);
          secondaryConfig.setKeyCreator(new KeyCreator(dataTable.getTypes(), i));
          String secName = name + i.toString();
          secondary.add(environment.openSecondaryDatabase(null, secName, primary, secondaryConfig));
          columns.add(schema.getColumn(i));
        }
        
        DbUnit dbUnit = new DbUnit(primary, secondary, columns);
        DATABASES.put(name, dbUnit);
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }
  
  public static void close() {
    if (environment == null) return;
    for (String name : DATABASES.keySet()) {
      DbUnit dbUnit = DATABASES.get(name);
      
      for (SecondaryDatabase secondaryDatabase : dbUnit.secondary)
        secondaryDatabase.close();
      
      dbUnit.primary.close();
      
      DATABASES.remove(name);
    }
    environment.close();
    environment = null;
  }
  
  /** This class may never be instantiated. */
  private DatabaseManager() {}
  
  private static class DbUnit {
    private Database primary;
    private List<SecondaryDatabase> secondary;
    private List<Column> columns;
    
    private DbUnit(Database primary, List<SecondaryDatabase> secondary, List<Column> columns) {
      this.primary = primary;
      this.secondary = secondary;
      this.columns = columns;
    }
  }
  
  public static SecondaryDatabase getSecondary(String name, Column column) {
    DbUnit unit = DATABASES.get(name);
    String columnName = column.getWholeColumnName().toLowerCase();
    
    for (int i = 0; i < unit.secondary.size(); i++) {
      if (columnName.equals(unit.columns.get(i).getWholeColumnName().toLowerCase())) {
        return unit.secondary.get(i);
      }
    }
    
    return null;
  }
}
