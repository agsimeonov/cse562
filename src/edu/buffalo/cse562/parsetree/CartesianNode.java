package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.CartesianIterator;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

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
    return new CartesianIterator((RowIterator) left.iterator(), (RowIterator) right.iterator());
  }

  @Override
  public Schema getSchema() {
    return new Schema(left.getSchema(), right.getSchema());
  }
}
