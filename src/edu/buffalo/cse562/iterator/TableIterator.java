package edu.buffalo.cse562.iterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;
import edu.buffalo.cse562.table.TableManager;

/**
 * Iterates over a data file one row at a time.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class TableIterator implements RowIterator {
  private final File              data;
  private final Schema            schema;
  private final ArrayList<String> types;
  private final String            alias;
  private BufferedReader          reader;

  /**
   * Initializes the iterator.
   * 
   * @param dataFile - valid data file to iterate over
   * @param tableSchema - table schema for the data file
   * @param types - list of types for each column in the table
   */
  public TableIterator(Table table) {
    DataTable dataTable = TableManager.getTable(table.getName());
    this.data = dataTable.getDataFile();
    this.schema = dataTable.getSchema();
    this.types = dataTable.getTypes();
    this.alias = table.getAlias();
    open();
  }
  
  @Override
  public boolean hasNext() {
    if (reader == null) return false;
    
    try {
      if (reader.ready()) {
        return true;
      } else {
        close();
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Row next() {
    try {
      if (!this.hasNext()) return null;
      
      Row row = new Row(schema.size());
      String[] data = this.hasNext() ? reader.readLine().split("\\|") : null;
      
      for (int i = 0; i < schema.size(); i++) {
        Column column = schema.getColumns().get(i);
        if (alias != null) column.getTable().setName(alias);
        String type = types.get(i).toLowerCase();
             
        switch (type) {
          case "int":
            row.setValue(i, new LongValue(Long.parseLong(data[i])));
            break;
          case "decimal":
            row.setValue(i, new DoubleValue(Double.parseDouble(data[i])));
            break;
          case "date":
            row.setValue(i, new DateValue("'" + data[i] + "'"));
            break;
          case "varchar":
          case "char":
          case "string":
            row.setValue(i, new StringValue("'" + data[i] + "'"));
            break;
          default:
            row.setValue(i, new NullValue());
        }
      }
      
      return row;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void close() {
    if (reader != null) {
      try {
        reader.close();
        reader = null;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void open() {
    try {
      if (reader == null) reader = new BufferedReader(new FileReader(data));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
