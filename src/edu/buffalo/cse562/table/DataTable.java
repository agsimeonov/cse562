package edu.buffalo.cse562.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;

/**
 * Manages the schema, type, and data file relationship for a table.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class DataTable {
  private ArrayList<String> types = new ArrayList<String>();
  private CreateTable       createTable;
  private Table             table;
  private String            name;
  private File              data;

  /**
   * Creates the table by storing its schema and associating it with a file.
   * 
   * @param createTable
   * @throws IOException
   */
  protected DataTable(CreateTable createTable) throws IOException {
    table = createTable.getTable();
    this.createTable = createTable;
    name = createTable.getTable().getName();
    
    for (Object o : createTable.getColumnDefinitions()) {
      ColumnDefinition columnDefinition = (ColumnDefinition) o;
      types.add(columnDefinition.getColDataType().getDataType());
    }
    
    // Make sure the data file exists
    Path path = Paths.get(TableManager.getDataDir(), name + ".dat");
    data = path.toFile();
    if (!data.exists()) {
      path = Paths.get(TableManager.getDataDir(), name.toLowerCase() + ".dat");
      data = path.toFile();
      if (!data.exists()) {
        path = Paths.get(TableManager.getDataDir(), name.toUpperCase() + ".dat");
        data = path.toFile();
        if (!data.exists()) {
          if (!data.createNewFile()) {
            throw new IOException("Could not create data file for table " + name);
          }
        }
      }            
    }
  }
  
  /**
   * Acquires a new instance of the table schema.
   * 
   * @return a new instance of the table schema
   */
  public Schema getSchema() {
    ArrayList<Column> columns = new ArrayList<Column>();
    ArrayList<Column> secondary = new ArrayList<Column>();
    Table tableCopy = new Table(table.getSchemaName(), table.getName());
    tableCopy.setAlias(table.getAlias());
    
    Schema out = new Schema(columns);
    Index primaryIndex = null;
    
    if (createTable.getIndexes() != null) {
      for (Object o : createTable.getIndexes()) {
        Index index = (Index) o;
        if (index.getType().toLowerCase().contains("primary")) {
          primaryIndex = index;
          out.setPrimaryKey(index);
          break;
        }
      }
    }
    
    for (Object o : createTable.getColumnDefinitions()) {
      ColumnDefinition columnDefinition = (ColumnDefinition) o;
      Column column = new Column(tableCopy, columnDefinition.getColumnName());
      columns.add(column);
      
      if (primaryIndex != null) {
        boolean found = false;
        for (Object name : primaryIndex.getColumnsNames()) {
          String columnName = (String) name;
          if (columnName.toLowerCase().equals(column.getWholeColumnName().toLowerCase()) ||
              columnName.toLowerCase().equals(column.getColumnName().toLowerCase())) {
            secondary.add(column);
            found = true;
            break;
          }
        }
        if (found) continue;
      }
      
      if (columnDefinition.getColumnSpecStrings() == null) continue;
      
      for (Object specString : columnDefinition.getColumnSpecStrings()) {
        
        if (((String) specString).toLowerCase().contains("reference")) {
          secondary.add(column);
          break;
        }
      }
    }
    
    out.setSecondaryKeys(secondary);
    
    return out;
  }
  
  /**
   * Acquires the table name.
   * 
   * @return the table name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Acquires the Table object for this table.
   * 
   * @return the Table object for this table
   */
  public Table getTable() {
    return table;
  }
  
  /**
   * Acquires the data file for this table.
   * 
   * @return the data file for this table
   */
  public File getDataFile() {
    return data;
  }
  
  /**
   * Acquires the types list for this table.
   * 
   * @return types list for this table
   */
  public ArrayList<String> getTypes() {
    return types;
  }
}
