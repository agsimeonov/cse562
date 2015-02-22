package edu.buffalo.cse562.iterator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Iterates over rows in a left and right children iterators and produces the Cartesian product.
 * This version of the Cartesian iterator employs concurrent techniques for faster computation.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class ConcurrentCartesianIterator implements RowIterator {
  private final RowIterator                    leftIterator;
  private final RowIterator                    rightIterator;
  private Row                                  leftRow;
  private Schema                               schema;
  private ArrayList<Row>                       buffer;
  private ArrayList<RunnableProduct>           threads;
  private LinkedBlockingQueue<LinkedList<Row>> products;
  private LinkedList<Row>                      currentProducts;

  /**
   * Initializes the iterator.
   * 
   * @param left - left child iterator
   * @param right - right child iterator
   */
  public ConcurrentCartesianIterator(RowIterator left, RowIterator right) {
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
        if (products != null && !products.isEmpty()) {
          return true;
        } else {
          if (currentProducts != null && !currentProducts.isEmpty()) {
            return true;
          } else {
            close();
            return false;
          }
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
      if (schema == null) schema = new Schema(leftRow.getSchema(), rightRow.getSchema());
      return new Row(schema, leftRow, rightRow);
    }
    
    rightIterator.close();
    
    while (leftIterator.hasNext()) {
      if (threads == null) threads = new ArrayList<RunnableProduct>();
      RunnableProduct thread = new RunnableProduct(leftIterator.next());
      threads.add(thread);
      thread.start();
    }
    
    if (threads != null) {
      for (Thread thread : threads) {
        try {
          thread.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    
    if (currentProducts == null || currentProducts.isEmpty()) {
      threads = null;
      buffer = null;
      currentProducts = products.remove();
    }
    
    return currentProducts.pop();
  }

  @Override
  public void close() {
    leftIterator.close();
    rightIterator.close();
    schema = null;
    buffer = null;
    products = null;
    threads = null;
    currentProducts = null;
  }

  @Override
  public void open() {
    leftIterator.open();
    rightIterator.open();
    buffer = new ArrayList<Row>();
    products = new LinkedBlockingQueue<LinkedList<Row>>();
  }
  
  private class RunnableProduct extends Thread {
    private final Row row;
    
    private RunnableProduct(Row row) {
      this.row = row;
    }

    @Override
    public void run() {
      LinkedList<Row> partial = new LinkedList<Row>();
      for (Row nextRow : buffer)
        partial.add(new Row(schema, row, nextRow));
      products.add(partial);
    }
  }
}
