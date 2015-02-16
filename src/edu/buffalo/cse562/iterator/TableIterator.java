package edu.buffalo.cse562.iterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Iterates over a data file one row at a time.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class TableIterator implements RowIterator {
  private File           data;
  private Schema         schema;
  private BufferedReader reader;

  /**
   * Initializes the iterator.
   * 
   * @param dataFile - valid data file to iterate over
   * @param tableSchema - table schema for the data file
   */
  public TableIterator(File dataFile, Schema tableSchema) {
    data = dataFile;
    schema = tableSchema;
    open();
  }
  
  @Override
  public boolean hasNext() {
    if (reader == null) return false;
    
    try {
      return reader.ready();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public Row next() {
    try {
      Row row = new Row(schema);
      String[] data = reader.readLine().split("\\|");      
      Iterator<Column> columnIterator = schema.getColumns().iterator();
      
      for (int i = 0; i < schema.size(); i++) {
        Column column = columnIterator.next();
        String type = schema.getColumnType(column).toLowerCase();
             
        switch (type) {
          case "int":
            row.setValue(column, new LongValue(Long.parseLong(data[i])));
            break;
          case "float":
            row.setValue(column, new DoubleValue(Double.parseDouble(data[i])));
            break;
          case "date":
            row.setValue(column, new DateValue("'" + data[i] + "'"));
            break;
          case "string":
            row.setValue(column, new StringValue("'" + data[i] + "'"));
          default:
            row.setValue(column, new NullValue());
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
    try {
      reader.close();
      reader = null;
    } catch (IOException e) {
      e.printStackTrace();
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
