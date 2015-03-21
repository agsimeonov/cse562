package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.iterator.CartesianIterator;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.SelectIterator;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

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
   * @param base - the parent node
   * @param expression - the join on expression statement
   */
  public JoinNode(ParseTree base, Expression expression) {
    super(base);
    this.expression = expression;
  }

  @Override
  public Iterator<Row> iterator() {
    RowIterator leftIterator = (RowIterator) left.iterator();
    RowIterator rightIterator = (RowIterator) right.iterator();
    CartesianIterator cartesianIterator = new CartesianIterator(leftIterator, rightIterator);
    return new SelectIterator(cartesianIterator, expression);
  }

  @Override
  public Schema getSchema() {
    return new Schema(left.getSchema(), right.getSchema());
  }
}
