package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse562.parsetree.CartesianNode;
import edu.buffalo.cse562.parsetree.ParseTree;

public class CrossToJoin {
  // get all cross
  // if select above cross (in sequence) has expression instanceof equals to
  // convert to join
  
  public static void convert(ParseTree node) {
    if (!(node instanceof CartesianNode)) return;
    if (node.getBase() == null) return;
    ParseTree parent = node.getBase();
    
  }
  
  public static List<CartesianNode> getAllCartesianNodes(ParseTree root) {
    List<CartesianNode> list = new ArrayList<CartesianNode>();
    
    if (root != null) {
      if (root instanceof CartesianNode) list.add((CartesianNode) root);
      if (root.getLeft() != null) list.addAll(getAllCartesianNodes(root.getLeft()));
      if (root.getRight() != null) list.addAll(getAllCartesianNodes(root.getRight()));
    }
    
    return list;
  }
}
