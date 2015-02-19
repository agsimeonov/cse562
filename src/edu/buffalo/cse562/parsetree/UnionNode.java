package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.iterator.DistinctIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.UnionIterator;
import edu.buffalo.cse562.table.Row;

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
    RowIterator leftIterator = (RowIterator) left.iterator();
    RowIterator rightIterator = (RowIterator) right.iterator();
    RowIterator union = new UnionIterator(leftIterator, rightIterator);    
    return new DistinctIterator(union);
  }
}
