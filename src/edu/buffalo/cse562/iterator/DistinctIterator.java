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
  private Row               next;

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
    if (next != null) return true;
    
    while (iterator.hasNext()) {
      Row nextRow = iterator.next();
      
      if (!buffer.contains(nextRow)) {
        buffer.add(nextRow);
        next = nextRow;
        return true;
      }
    }
    
    close();
    return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row row = next;
    next = null;
    return row;
  }

  @Override
  public void close() {
    iterator.close();
    buffer = null;
  }

  @Override
  public void open() {
    if (buffer != null) return;
    iterator.open();
    buffer = new HashSet<Row>();
  }
}
