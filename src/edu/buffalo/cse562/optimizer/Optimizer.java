package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse562.parsetree.ParseTree;

public class Optimizer {
  public static void optimize(ParseTree root) {
    SplitSelect.splitAllSelectNodes(root);
    System.out.println(root);
    MoveDownSelect.moveDownAllSelectNodes(root);
    System.out.println(root);
    CrossToJoin.crossToJoin(root);
  }
  
  public static void popNode(ParseTree node) {
    ParseTree parent = node.getBase();
    ParseTree child = node.getLeft();
    
    if (parent != null) {
      if (parent.getLeft() == node) parent.setLeft(child);
      else parent.setRight(child);
    }
    if (child != null) child.setBase(parent);
  }
  
  public static void pushNode(ParseTree node, ParseTree parent, ParseTree left, ParseTree right) {
    if (parent != null) {
      if (parent.getLeft() == left) parent.setLeft(node);
      else parent.setRight(node);
    }
    
    if (left != null) left.setBase(node);
    if (right != null) right.setBase(node);
    
    node.setBase(parent);
    node.setLeft(left);
    node.setRight(right);
  }
  
  public static List<ParseTree> getAllTypeNodes(ParseTree root, Class<?> type) {
    List<ParseTree> list = new ArrayList<ParseTree>();
    
    if (root != null) {
      if (root.getClass().isAssignableFrom(type)) list.add(root);
      if (root.getLeft() != null) list.addAll(getAllTypeNodes(root.getLeft(), type));
      if (root.getRight() != null) list.addAll(getAllTypeNodes(root.getRight(), type));
    }
    
    return list;
  }
}
