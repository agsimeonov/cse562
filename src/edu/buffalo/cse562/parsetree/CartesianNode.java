package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.iterator.ConcurrentCartesianIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;

/**
 * A Cartesian product node.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class CartesianNode extends ParseTree {
  /**
   * Initializes the Cartesian product node.
   * 
   * @param base - the parent node
   */
  public CartesianNode(ParseTree base) {
    super(base);
  }

  @Override
  public Iterator<Row> iterator() {
    return new ConcurrentCartesianIterator((RowIterator) left.iterator(), (RowIterator) right.iterator());
  }
}
