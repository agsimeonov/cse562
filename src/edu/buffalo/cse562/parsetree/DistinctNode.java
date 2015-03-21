package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.iterator.DistinctIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Handles distinct calls on its child.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class DistinctNode extends ParseTree {
  /**
   * Initializes the distinct node.
   * 
   * @param base - the parent node
   */
  public DistinctNode(ParseTree base) {
    super(base);
  }

  @Override
  public Iterator<Row> iterator() {
    return new DistinctIterator((RowIterator) this.getLeft().iterator());
  }

  @Override
  public Schema getSchema() {
    return left.getSchema();
  }
}
