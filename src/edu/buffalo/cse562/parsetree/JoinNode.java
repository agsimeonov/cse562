package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.table.Row;

public class JoinNode extends ParseTree {
  private final Expression expression;
  
  /**
   * Initializes the Join node.
   * 
   * @param base - the parent node
   * @param expression - 
   */
  public JoinNode(ParseTree base, Expression expression) {
    super(base);
    this.expression = expression;
  }

  @Override
  public Iterator<Row> iterator() {
    
    return null;
  }
}
