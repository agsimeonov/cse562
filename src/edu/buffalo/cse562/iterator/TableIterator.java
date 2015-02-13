package edu.buffalo.cse562.iterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;

public class TableIterator implements SQLIterator {
  private File           data;
  private String[]       types;
  private BufferedReader reader;

  public TableIterator(File dataFile, String[] dataTypes) {
    data = dataFile;
    types = dataTypes;
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
  public LeafValue[] next() {
    try {
      LeafValue[] row = new LeafValue[types.length];
      
      if (!hasNext()) {
        for (int i = 0; i < types.length; i++)
          row[i] = new NullValue();
        
        return row;
      }
      
      String[] data = reader.readLine().split("\\|");
      
      for (int i = 0; i < types.length; i++) {
        String type = types[i].toLowerCase();
        if (type.equals("int")) row[i] = new LongValue(Long.parseLong(data[i]));
        else if (type.equals("double")) row[i] = new DoubleValue(Double.parseDouble(data[i]));
        else if (type.equals("string")) row[i] = new StringValue(data[i]);
        else if (type.equals("date")) row[i] = new DateValue(data[i]);
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
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void open() {
    try {
      reader = new BufferedReader(new FileReader(data));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
