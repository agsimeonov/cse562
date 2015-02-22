package edu.buffalo.cse562.iterator;

import net.sf.jsqlparser.statement.select.Limit;
import edu.buffalo.cse562.table.Row;

/**
 * Iterates over rows and returns them up until a set limit.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class LimitIterator implements RowIterator {
  private final RowIterator iterator;
  private final Limit       limit;
  private long              max   = 0;
  private long              count = 0;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param limit - the set limit
   */
  public LimitIterator(RowIterator iterator, Limit limit) {
    this.iterator = iterator;
    this.limit = limit;
    open();
  }

  @Override
  public boolean hasNext() {
    if (!iterator.hasNext() || max == 0 || count >= max) {
      close();
      return false;
    }
    return true;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    count = count + 1;
    return iterator.next();
  }

  @Override
  public void close() {
    iterator.close();
    max = 0;
    count = 0;
  }

  @Override
  public void open() {
    if (max == 0) {
      iterator.open();
      max = limit.getRowCount();
      count = 0;
    }
  }
}
