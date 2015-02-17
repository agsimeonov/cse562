package edu.buffalo.cse562.iterator;

import edu.buffalo.cse562.table.Row;

/**
 * Iterates over rows in a left and right children iterators and produces concatenated rows.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class ConcatIterator implements RowIterator {
  private RowIterator left;
  private RowIterator right;

  /**
   * Initializes the iterator.
   * 
   * @param left - left child iterator
   * @param right - right child iterator
   */
  public ConcatIterator(RowIterator left, RowIterator right) {
    this.left = left;
    this.right = right;
    open();
  }
  
  @Override
  public boolean hasNext() {
    return left.hasNext() || right.hasNext();
  }

  @Override
  public Row next() {
    return left.next().concat(right.next());
  }

  @Override
  public void close() {
    left.close();
    right.close();
  }

  @Override
  public void open() {
    left.open();
    right.open();
  }
}
