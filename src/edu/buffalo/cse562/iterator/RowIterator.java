package edu.buffalo.cse562.iterator;

import java.util.Iterator;

import edu.buffalo.cse562.table.Row;

/**
 * Acquires results one row at a time.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public interface RowIterator extends Iterator<Row> {
  /**
   * This method end the iteration after all tuples, or all tuples that are consumer wanted, have
   * been obtained.  Typically, it calls {@link #close()} on any arguments of the operator.
   */
  public void close();
  
  /**
   * This method starts the process of getting tuples, but does not get a tuple.  It initializes
   * any data structures needed to perform the operations and calls {@link #open()} for any 
   * arguments of the operation.  It does not restart them if they have already been initialized so 
   * you must call {@link #close()} first to restart the iterator.
   */
  public void open();
}
