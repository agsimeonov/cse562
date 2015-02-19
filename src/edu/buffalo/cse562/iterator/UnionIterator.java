package edu.buffalo.cse562.iterator;

import edu.buffalo.cse562.table.Row;

/**
 * Performs non-set union on two given child iterators.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class UnionIterator implements RowIterator {
  private final RowIterator leftIterator;
  private final RowIterator rightIterator;

  /**
   * Initializes the iterator.
   * 
   * @param left - left child iterator
   * @param right - right child iterator
   */
  public UnionIterator(RowIterator left, RowIterator right) {
    this.leftIterator = left;
    this.rightIterator = right;
    open();
  }
  
  @Override
  public boolean hasNext() {
    if (leftIterator.hasNext()) return true;
    if (rightIterator.hasNext()) return true;
    close();
    return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    return (leftIterator.hasNext()) ? leftIterator.next() : rightIterator.next();
  }

  @Override
  public void close() {
    leftIterator.close();
    rightIterator.close();
  }

  @Override
  public void open() {
    leftIterator.open();
    rightIterator.open();
  }
}
