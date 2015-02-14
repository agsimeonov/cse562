package edu.buffalo.cse562.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import net.sf.jsqlparser.statement.create.table.CreateTable;
import edu.buffalo.cse562.iterator.TableIterator;

public class DataTable implements Iterable<Row> {
  private Schema schema;
  private String   name;
  private File     data;

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
  
  public Schema getSchema() {
    return schema;
  }
  
  public String getName() {
    return name;
  }

  @Override
  public Iterator<Row> iterator() {
    return new TableIterator(data, schema);
  }
}
