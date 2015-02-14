package edu.buffalo.cse562.iterator;

import java.util.Iterator;

import edu.buffalo.cse562.table.Row;

public interface SQLIterator extends Iterator<Row> {
  public void close();
  
  public void open();
}
