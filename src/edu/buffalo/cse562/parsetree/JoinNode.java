package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.SelectionIterator;
import edu.buffalo.cse562.table.Row;

/**
 * A node that handles simple joins.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class JoinNode extends ParseTree {
  private final Expression expression;
  
  /**
   * Initializes the Join node.
   * 
   * @param child - the child node formed by the from items list
   * @param expression - the join on expression statement
   */
  public JoinNode(ParseTree child, Expression expression) {
    super(null);
    setLeft(child);
    this.expression = expression;
  }

  @Override
  public Iterator<Row> iterator() {
    return new SelectionIterator((RowIterator) this.getLeft().iterator(), expression);
  }
}
