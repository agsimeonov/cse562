package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.UnionIterator;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * A Union node.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class UnionNode extends ParseTree {
  /**
   * Initializes the Union product node.
   * 
   * @param base - the parent node
   */
  public UnionNode(ParseTree base) {
    super(base);
  }

  @Override
  public Iterator<Row> iterator() {
    return new UnionIterator((RowIterator) left.iterator(), (RowIterator) right.iterator());
  }

  @Override
  public Schema getSchema() {
    return left.getSchema();
  }

  @Override
  public String nodeString() {
    return "∪";
  }
}
