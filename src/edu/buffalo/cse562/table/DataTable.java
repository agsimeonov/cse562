package edu.buffalo.cse562.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import edu.buffalo.cse562.iterator.TableIterator;

public class DataTable implements Iterable<LeafValue[]> {
  private Column[] schema;
  private String[] types;
  private String   name;
  private File     data;

  protected DataTable(CreateTable createTable) throws IOException {
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
    
    @SuppressWarnings("unchecked")
    List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
    int numColumns = columnDefinitions.size();
    schema = new Column[numColumns];
    types = new String[numColumns];
    Table table = createTable.getTable();
    
    for (int i = 0; i < numColumns; i++) {
      schema[i] = new Column(table, columnDefinitions.get(i).getColumnName());
      types[i] = columnDefinitions.get(i).getColDataType().getDataType();
    }
    
    //TESTING ITERATOR
//    for(Object o : this) {
//      LeafValue[] lon = (LeafValue[]) o;
//      try {
//        System.out.println(lon[0].toLong());
//      } catch (InvalidLeaf e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//      }
//    }
  }
  
  public Column[] getSchema() {
    return schema;
  }
  
  public String getName() {
    return name;
  }

  @Override
  public Iterator<LeafValue[]> iterator() {
    return new TableIterator(data, types);
  }
}
