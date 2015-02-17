package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.ConcatIterator;
import edu.buffalo.cse562.table.Row;

/**
 * A union node denoting that its children must undergo concatenation.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class ConcatNode extends ParseTree {
  /**
   * Initializes the union node.
   * 
   * @param base - the parent node
   */
  public ConcatNode(ParseTree base) {
    super(base);
  }

  @Override
  public Iterator<Row> iterator() {
    return new ConcatIterator((RowIterator) left.iterator(), (RowIterator) right.iterator());
  }
}
