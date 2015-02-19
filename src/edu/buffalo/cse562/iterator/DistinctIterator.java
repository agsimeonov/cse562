package edu.buffalo.cse562.iterator;

import java.util.HashSet;

import edu.buffalo.cse562.table.Row;

public class DistinctIterator implements RowIterator {
  private final RowIterator iterator;
  private HashSet<Row>      buffer;

  public DistinctIterator(RowIterator iterator) {
    this.iterator = iterator;
    open();
  }

  @Override
  public boolean hasNext() {
    if (buffer == null) return false;
    
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
    buffer = null;
  }

  @Override
  public void open() {
    if (buffer == null) {
      iterator.open();
      buffer = new HashSet<Row>(); 
      while (iterator.hasNext())
        buffer.add(iterator.next());
    }
  }
}
