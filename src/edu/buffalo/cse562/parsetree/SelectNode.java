package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.iterator.RowIterator;
import edu.buffalo.cse562.iterator.SelectIterator;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Handles selection operation for rows in the child iterator given a where or having expression.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class SelectNode extends ParseTree {
  protected final Expression expression;

  /**
   * Initializes the selection node.
   * 
   * @param base - the parent node
   * @param expression - where or having expression
   */
  public SelectNode(ParseTree base, Expression expression) {
    super(base);
    this.expression = expression;
  }

  @Override
  public Iterator<Row> iterator() {
    return new SelectIterator((RowIterator) left.iterator(), expression, left.getSchema());
  }

  @Override
  public Schema getSchema() {
    return left.getSchema();
  }

  @Override
  public String nodeString() {
    return "σ";
  }
}
