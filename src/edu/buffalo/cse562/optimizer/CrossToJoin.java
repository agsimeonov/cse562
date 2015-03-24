package edu.buffalo.cse562.optimizer;

import java.util.List;

import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import edu.buffalo.cse562.parsetree.CartesianNode;
import edu.buffalo.cse562.parsetree.JoinNode;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.SelectNode;

public class CrossToJoin {
  public static void crossToJoin(ParseTree root) {
    List<ParseTree> cartesianNodes = Optimizer.getAllTypeNodes(root, CartesianNode.class);
    for (ParseTree node : cartesianNodes)
      convert((CartesianNode) node);
  }
  
  public static void convert(CartesianNode node) {
    if (!(node instanceof CartesianNode)) return;
    SelectNode parent = getSelectParent(node.getBase());
    if (parent == null) return;
    Optimizer.popNode(parent);
    Optimizer.popNode(node);
    ParseTree base = node.getLeft().getBase();
    ParseTree join = new JoinNode(base, node.getLeft(), node.getRight(), parent.getExpression());
    Optimizer.pushNode(join, join.getBase(), join.getLeft(), join.getRight());
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
