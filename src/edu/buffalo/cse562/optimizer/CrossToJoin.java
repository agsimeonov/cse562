package edu.buffalo.cse562.optimizer;

import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import edu.buffalo.cse562.parsetree.CartesianNode;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.SelectNode;

public class CrossToJoin {
  // get all cross
  // if select above cross (in sequence) has expression instanceof equals to
  // convert to join
  
  public static void convert(CartesianNode node) {
    if (!(node instanceof CartesianNode)) return;
    SelectNode parent = getSelectParent(node);
    if (parent == null) return;

    
  }
  
  /**
   * Acquires a valid selection parent used for cross to join conversion.  To qualify it must
   * contain an AndExpression and be in a sequence of select nodes, returns null if none such.
   * 
   * @param parent - the first parent to check
   * @return a valid selection parent, null if one does not exist
   */
  public static SelectNode getSelectParent(ParseTree parent) {
    if (parent == null) return null;
    if (!(parent instanceof SelectNode)) return null;
    if (((SelectNode) parent).getExpression() instanceof EqualsTo) return (SelectNode) parent;
    else return getSelectParent(parent.getBase());
  }
}
