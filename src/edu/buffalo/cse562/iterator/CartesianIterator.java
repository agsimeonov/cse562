package edu.buffalo.cse562.iterator;

import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Iterates over rows in a left and right children iterators and produces the Cartesian product.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class CartesianIterator implements RowIterator {
  private final RowIterator leftIterator;
  private final RowIterator rightIterator;
  private Row               leftRow;
  private Schema            schema;

  /**
   * Initializes the iterator.
   * 
   * @param left - left child iterator
   * @param right - right child iterator
   */
  public CartesianIterator(RowIterator left, RowIterator right) {
    this.leftIterator = left;
    this.rightIterator = right;
    open();
  }
  
  @Override
  public boolean hasNext() {
    if (leftIterator.hasNext()) {
      return true;
    } else {
      if (rightIterator.hasNext()) {
        return true;
      } else {
        close();
        return false;
      }
    }
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    if (leftRow == null) leftRow = leftIterator.next();
    if (!rightIterator.hasNext()) {
      leftRow = leftIterator.next();
      rightIterator.close();
      rightIterator.open();
    }
    
    Row rightRow = rightIterator.next();
    if (schema == null) schema = new Schema(leftRow.getSchema(), rightRow.getSchema());
    return new Row(schema, leftRow, rightRow);
  }

  @Override
  public void close() {
    leftIterator.close();
    rightIterator.close();
    schema = null;
  }

  @Override
  public void open() {
    leftIterator.open();
    rightIterator.open();
  }
}
