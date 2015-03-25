package edu.buffalo.cse562.iterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
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
  private final File                      data;
  private final Schema                    schema;
  private final ArrayList<String>         types;
  private BufferedReader                  reader;
  private final HashMap<Integer, Integer> indexLookup;

  /**
   * Initializes the iterator.
   * 
   * @param dataFile - valid data file to iterate over
   * @param tableSchema - table schema for the data file
   * @param types - list of types for each column in the table
   * @param optimal - the optimal schema
   */
  public TableIterator(Table table, Schema optimal) {
    DataTable dataTable = TableManager.getTable(table.getName());
    this.data = dataTable.getDataFile();
    this.schema = dataTable.getSchema();
    this.types = dataTable.getTypes();
    HashMap<String, Integer> schemaLookup = schema.getLookupTable();
    HashMap<String, Integer> optimalLookup = optimal.getLookupTable();
    this.indexLookup = new HashMap<Integer, Integer>();
    for (String name : optimalLookup.keySet())
      indexLookup.put(schemaLookup.get(name), optimalLookup.get(name));
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
      
      Row row = new Row(indexLookup.size());
      String[] data = this.hasNext() ? reader.readLine().split("\\|") : null;
      
      for (int i = 0; i < schema.size(); i++) {
        Integer index = indexLookup.get(i);
        if (index == null) continue;
        String type = types.get(i).toLowerCase();
             
        switch (type) {
          case "int":
            row.setValue(index, new LongValue(Long.parseLong(data[i])));
            break;
          case "decimal":
            row.setValue(index, new DoubleValue(Double.parseDouble(data[i])));
            break;
          case "date":
            row.setValue(index, new DateValue("'" + data[i] + "'"));
            break;
          case "varchar":
          case "char":
          case "string":
            row.setValue(index, new StringValue("'" + data[i] + "'"));
            break;
          default:
            row.setValue(index, new NullValue());
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
