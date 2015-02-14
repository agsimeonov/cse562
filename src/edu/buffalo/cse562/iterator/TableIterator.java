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
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

public class TableIterator implements SQLIterator {
  private File           data;
  private Schema         schema;
  private BufferedReader reader;

  public TableIterator(File dataFile, Schema tableSchema) {
    data = dataFile;
    schema = tableSchema;
    open();
  }
  
  @Override
  public boolean hasNext() {
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
      
      for (int i = 0; i < schema.numColumns(); i++) {
        Column column = columnIterator.next();
        String type = schema.getColumnType(column).toLowerCase();
        String name = column.getWholeColumnName();
        
        switch (type) {
          case "int":
            row.addColumnValue(name, new LongValue(Long.parseLong(data[i])));
            break;
          case "float":
            row.addColumnValue(name, new DoubleValue(Double.parseDouble(data[i])));
            break;
          case "date":
            row.addColumnValue(name, new DateValue("\"" + data[i] + "\""));
            break;
          default:
            row.addColumnValue(name, new StringValue("\"" + data[i] + "\""));
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
