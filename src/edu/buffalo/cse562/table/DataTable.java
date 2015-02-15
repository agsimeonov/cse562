package edu.buffalo.cse562.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import net.sf.jsqlparser.statement.create.table.CreateTable;
import edu.buffalo.cse562.iterator.TableIterator;

/**
 * Manages the schema, and data file relationship for a given table, and provides an iterator over
 * its rows.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class DataTable implements Iterable<Row> {
  private Schema schema;
  private String   name;
  private File     data;

  /**
   * Creates the table by storing its schema and associating it with a file.
   * 
   * @param createTable
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  protected DataTable(CreateTable createTable) throws IOException {
    schema = new Schema(createTable.getTable(), createTable.getColumnDefinitions());
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

  @Override
  public Iterator<Row> iterator() {
    return new TableIterator(data, schema);
  }
}
