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
  private final Schema outSchema;
  
  /**
   * Initializes the Join node.
   * 
   * @param base - the parent node
   * @param left - left node
   * @param right - right node
   * @param expression - the join on expression statement
   */
  public JoinNode(ParseTree base, ParseTree left, ParseTree right, Expression expression) {
    super(base);
    super.left = left;
    super.right = right;
    this.expression = expression;
    outSchema = new Schema(left.getSchema(), right.getSchema());
  }

  @Override
  public Iterator<Row> iterator() {
    RowIterator leftIterator = (RowIterator) left.iterator();
    RowIterator rightIterator = (RowIterator) right.iterator();
    CartesianIterator cartesianIterator = new CartesianIterator(leftIterator, rightIterator);
    return new SelectIterator(cartesianIterator, expression, outSchema);
  }

  @Override
  public Schema getSchema() {
    return outSchema;
  }
}
