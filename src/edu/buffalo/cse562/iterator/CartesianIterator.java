package edu.buffalo.cse562.iterator;

import java.util.ArrayList;
import java.util.Iterator;

import edu.buffalo.cse562.table.Row;

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
  private ArrayList<Row>    buffer;
  private Iterator<Row>     bufferIterator;

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
        if (bufferIterator != null && bufferIterator.hasNext()) {
          return true;
        } else {
          close();
          return false;
        }
      }
    }
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    if (leftRow == null) leftRow = leftIterator.next();
    
    if (rightIterator.hasNext()) {
      Row rightRow = rightIterator.next();
      buffer.add(rightRow);
      return new Row(leftRow, rightRow);
    }
    
    rightIterator.close();
    
    if (bufferIterator == null) {
      bufferIterator = buffer.iterator();
      leftRow = leftIterator.next();
    }
    
    if (!bufferIterator.hasNext()) {
      leftRow = leftIterator.next();
      bufferIterator = buffer.iterator();
    }
    
    Row rightRow = bufferIterator.next();
    return new Row(leftRow, rightRow);
  }

  @Override
  public void close() {
    leftIterator.close();
    rightIterator.close();
    buffer = null;
    bufferIterator = null;
  }

  @Override
  public void open() {
    leftIterator.open();
    rightIterator.open();
    buffer = new ArrayList<Row>();
  }
}
