package edu.buffalo.cse562.iterator;

import edu.buffalo.cse562.table.Row;

public class UnionIterator implements RowIterator {
  private RowIterator left;
  private RowIterator right;

  public UnionIterator(RowIterator left, RowIterator right) {
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
