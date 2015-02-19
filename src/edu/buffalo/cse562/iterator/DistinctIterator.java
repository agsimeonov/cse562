package edu.buffalo.cse562.iterator;

import java.util.HashSet;

import edu.buffalo.cse562.table.Row;

/**
 * This iterator outputs the distinct set of rows in its child iterator.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class DistinctIterator implements RowIterator {
  private final RowIterator iterator;
  private HashSet<Row>      buffer;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   */
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

  /**
   * Builds a buffer of distinct rows to be used for the iteration.
   */
  @Override
  public void open() {
    if (buffer != null) return;
    iterator.open();
    buffer = new HashSet<Row>();
    while (iterator.hasNext()) {
      Row next = iterator.next();
      System.out.println(next.hashCode());
      buffer.add(next);
    }
  }
}
