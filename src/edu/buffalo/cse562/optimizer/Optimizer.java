package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.SelectNode;

public class Optimizer {
  public static void optimize(ParseTree root) {
    SplitSelect.splitAllSelectNodes(root);
    System.out.println(root);
    MoveDownSelect.moveDownAllSelectNodes(root);
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
  
  public static void pushNode(ParseTree node, boolean left, ParseTree parent, ParseTree child) {
    if (parent != null) {
      if (parent.getLeft() == child) parent.setLeft(node);
      else parent.setRight(node);
    }
    
    if (child != null) child.setBase(node);
    
    node.setBase(parent);
    if (left) node.setLeft(child);
    else node.setRight(child);
  }
  
  public static List<ParseTree> getAllTypeNodes(ParseTree root, Object type) {
    List<ParseTree> list = new ArrayList<ParseTree>();
    
    if (root != null) {
      if (root.getClass().isInstance(type)) list.add((SelectNode) root);
      if (root.getLeft() != null) list.addAll(getAllTypeNodes(root.getLeft(), type));
      if (root.getRight() != null) list.addAll(getAllTypeNodes(root.getRight(), type));
    }
    
    return list;
  }
}
