package edu.buffalo.cse562.iterator;

import java.util.HashSet;

import edu.buffalo.cse562.table.Row;

public class DistinctIterator implements RowIterator {
  private RowIterator  iterator;
  private HashSet<Row> buffer = new HashSet<Row>();

  public DistinctIterator(RowIterator iterator) {
    this.iterator = iterator;
    while (iterator.hasNext())
      buffer.add(iterator.next());
  }
  
  @Override
  public boolean hasNext() {
    if (buffer.isEmpty()) {
      close();
      return false;
    } else {
      return true;
    }
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row row = buffer.iterator().next();
    buffer.remove(row);
    return row;
  }

  @Override
  public void close() {
    iterator.close();
  }

  @Override
  public void open() {
    iterator.open();
  }
}
