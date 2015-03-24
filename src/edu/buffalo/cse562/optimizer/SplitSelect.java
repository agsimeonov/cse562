package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.SelectNode;

/**
 * Used to perform the conjunctive selection split optimization.
 * 
 * @author Alexander Simeonov
 */
public class SplitSelect {
  /**
   * Splits all selection nodes within the given tree.
   * 
   * @param root - root of the given tree
   */
  public static void splitAllSelectNodes(ParseTree root) {
    List<ParseTree> selectNodes = Optimizer.getAllTypeNodes(root, SelectNode.class);
    for (ParseTree node : selectNodes)
      split((SelectNode) node);
  }
  
  /**
   * Splits a given selection node.
   * 
   * @param selectNode - node to split
   */
  public static void split(SelectNode selectNode) {
    List<Expression> list = splitConjunctive(selectNode.getExpression());
    ParseTree parent = selectNode.getBase();
    ParseTree child = selectNode.getLeft();
    Optimizer.popNode(selectNode);
    
    for (Expression expression : list) {
      SelectNode node = new SelectNode(parent, expression);
      Optimizer.pushNode(node, parent, child, null);
      parent = node;
    }
  }

  /**
   * Splits an expression conjunctively into a list of expressions.
   * 
   * @param expression - the given expression to split
   * @return a list of expressions
   */
  public static List<Expression> splitConjunctive(Expression expression) {
    List<Expression> list = new ArrayList<Expression>();
    
    if (expression instanceof AndExpression) {
      AndExpression and = (AndExpression) expression;
      list.addAll(splitConjunctive(and.getLeftExpression()));
      list.addAll(splitConjunctive(and.getRightExpression()));
    } else {
      list.add(expression);
    }
    
    return list;
  }
}
