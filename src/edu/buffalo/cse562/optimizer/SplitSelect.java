package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.SelectNode;

public class SplitSelect {
  public static void splitAllSelectNodes(ParseTree root) {
    List<SelectNode> selectNodes = getAllSelectNodes(root);
    for (SelectNode node : selectNodes)
      split(node);
  }
  
  public static List<SelectNode> getAllSelectNodes(ParseTree root) {
    List<SelectNode> list = new ArrayList<SelectNode>();
    
    if (root != null) {
      if (root instanceof SelectNode) list.add((SelectNode) root);
      if (root.getLeft() != null) list.addAll(getAllSelectNodes(root.getLeft()));
      if (root.getRight() != null) list.addAll(getAllSelectNodes(root.getRight()));
    }
    
    return list;
  }
  
  public static void split(SelectNode selectNode) {
    List<Expression> list = splitConjunctive(selectNode.getExpression());
    ParseTree parent = selectNode.getBase();
    ParseTree child = selectNode.getLeft();
    if (parent != null) parent.setLeft(child);
    if (child != null) child.setBase(parent);
    
    for (Expression expression : list) {
      SelectNode node = new SelectNode(parent, expression);
      if (parent != null) parent.setLeft(node);
      node.setLeft(child);
      if (child != null) child.setBase(node);
      parent = node;
    }
  }

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
