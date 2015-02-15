package edu.buffalo.cse562.iterator;

import java.util.Iterator;

import edu.buffalo.cse562.table.Row;

/**
 * Acquires results one row at a time.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public interface SQLIterator extends Iterator<Row> {
  /**
   * This method end the iteration after all tuples, or all tuples that are consumer wanted, have
   * been obtained.  Typically, it calls close() on any arguments of the operator.
   */
  public void close();
  
  /**
   * This method starts the process of getting tuples, but does not get a tuple.  It initializes
   * any data structures needed to perform the operations and calls open() for any arguments of
   * the operation.
   */
  public void open();
}
