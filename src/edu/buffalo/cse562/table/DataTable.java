package edu.buffalo.cse562.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import edu.buffalo.cse562.iterator.TableIterator;

/**
 * Manages the schema, data file relationship for a table, and provides an iterator over its rows.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class DataTable implements Iterable<Row> {
  private ArrayList<Column> columns = new ArrayList<Column>();
  private ArrayList<String> types   = new ArrayList<String>();
  private Schema            schema;
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
    
    for (Object o : createTable.getColumnDefinitions()) {
      ColumnDefinition columnDefinition = (ColumnDefinition) o;
      columns.add(new Column(table, columnDefinition.getColumnName()));
      types.add(columnDefinition.getColDataType().getDataType());
    }
    
    schema = new Schema(columns);
    name = createTable.getTable().getName();
    
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
   * Acquires the table schema.
   * 
   * @return the table schema
   */
  public Schema getSchema() {
    return schema;
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

  @Override
  public Iterator<Row> iterator() {
    return new TableIterator(data, schema, types);
  }
}
